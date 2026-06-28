function fetchNextVideoChunk(url,videoFile, startByte) {
    // 返回一个Promise，该Promise解析为Blob对象
    let param={}
    param['u']=window.selectUserItem.dataset.username
    param['p']=videoFile
    param['s']=startByte
    param['l']=1024*1024*4
    return  fetch(url,{
        method: 'post',
        body: JSON.stringify(param)
    }).then(response=>{
        if (response.ok){
            return response.blob()
        }else {
            throw new Error("null");
        }
    })
}

// 创建MediaSource对象
// const mediaSource = new MediaSource();
// const videoPlayer = document.getElementById('videoPlayer');
// let videoUrl="http://" + address + ":" + port + "/map/userShow/getVideo"
// videoPlayer.src = URL.createObjectURL(mediaSource);
// let videoFile=''
function playVideo(path) {
    videoFile=path
    mediaSource.addEventListener('sourceopen', function() {
        const sourceBuffer = mediaSource.addSourceBuffer('video/mp4; codecs="avc1.42E01E, mp4a.40.2"');

        // 初始化变量
        let startByte = 0;
        // 函数，用于加载下一个视频块
        function loadNextChunk() {
            fetchNextVideoChunk(videoUrl,videoFile, startByte).then(blob => {
                sourceBuffer.appendBuffer(blob);
                startByte += blob.size; // 更新下一个块的起始字节
            }).catch(error => {
                console.error('Error loading video chunk:', error);
            });
        }

        // 监听缓冲区更新事件，并在缓冲区更新完成后加载下一个块
        sourceBuffer.addEventListener('updateend', function() {
            if (!sourceBuffer.updating && mediaSource.readyState === 'open') {
                loadNextChunk();
            }
        });

        // 开始加载第一个视频块
        loadNextChunk();
    });
}
// 当MediaSource对象打开时，设置sourceBuffer

