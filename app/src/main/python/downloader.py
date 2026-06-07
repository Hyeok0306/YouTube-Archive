import sys, os, json, re

# 엔진 초기화 (Chaquopy 전용)
def init_engine(app_dir):
    target_dir = os.path.join(app_dir, "yt_dlp_latest")
    os.makedirs(target_dir, exist_ok=True)
    if target_dir not in sys.path: sys.path.insert(0, target_dir)

# 외부 라이브러리(yt-dlp) 설치 및 업데이트
def update_yt_dlp():
    try:
        from pip._internal import main as pipmain
        # yt-dlp 설치 및 업그레이드
        pipmain(['install', '--upgrade', '--quiet', '--target', sys.path[0], 'yt-dlp'])
        return json.dumps({"status": "success", "message": "yt-dlp is up to date"})
    except Exception as e:
        return json.dumps({"status": "error", "message": str(e)})

# 영상 정보 추출
def get_video_info(url):
    import yt_dlp
    ydl_opts = {'quiet': True, 'extract_flat': True}
    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        info = ydl.extract_info(url, download=False)
        if 'entries' in info:
            entries = [{"title": e.get('title'), "url": e.get('url') or f"https://www.youtube.com/watch?v={e.get('id')}"} for e in info['entries'] if e]
            return json.dumps({"status": "success", "type": "playlist", "entries": entries})
        return json.dumps({"status": "success", "type": "video", "title": info.get('title'), "url": url})

# 최고 화질 다운로드 (ffmpeg 불필요 방식)
def start_download(url, save_path, dtype, tid, java_cb=None):
    import yt_dlp

    def hook(d):
        if java_cb and d['status'] == 'downloading':
            p = re.sub(r'[^\d.]', '', d.get('_percent_str', '0'))
            if p:
                try: java_cb.onProgressUpdate(int(float(p)))
                except: pass

    suffix = '_v' if dtype == 'video' else '_a'

    # ⭕ 핵심 수정: ffmpeg가 없어도 되는 '단일 스트림' 선택
    # [ext=mp4] 형식이면서 하나로 합쳐진 파일을 선택합니다.
    if dtype == 'video':
        fmt = 'best[ext=mp4]/best'
    else:
        fmt = 'bestaudio[ext=m4a]/bestaudio'

    opts = {
        'format': fmt,
        'outtmpl': f'{save_path}/{tid}{suffix}.%(ext)s',
        'progress_hooks': [hook],
        'quiet': True,
        'noprogress': True,      # 안드로이드 로그 간섭 방지
        'no_warnings': True
    }

# 기존 start_download 함수 맨 끝부분을 이렇게 수정하세요
    with yt_dlp.YoutubeDL(opts) as ydl:
        info = ydl.extract_info(url, download=True)
        file_path = ydl.prepare_filename(info)

        # ⭕ 파일이 실제로 존재하는지 체크하고 로그를 남김
        if os.path.exists(file_path):
            print(f"DEBUG: File successfully created at: {file_path}") # Logcat에서 확인 가능
            return json.dumps({"status": "success", "file_path": file_path})
        else:
            print(f"DEBUG: File creation failed at: {file_path}")
            return json.dumps({"status": "error", "message": "File not found after download"})