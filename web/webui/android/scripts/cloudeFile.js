// let container=document.querySelector('.dragContainer');
// container.ondragstart=e=>{
//     console.log("start",e.log);
// }
let drageableds = document.querySelectorAll(".left ul div");
let dropeableds = document.querySelectorAll(".right li");
let dropout = document.querySelector("#outdrag");
// let originalPosition = {
//   left: drageableds.offsetLeft,
//   top: drageableds.offsetTop
// };
let leftdiv = {}

function createLeftdiv(target, absolute) {
    let div = document.createElement("div");
    div.innerText = target
    div.dataset.target = target
    div.dataset.absolute = absolute
    leftdiv[absolute + target] = div;
    div.setAttribute('class','dragleft')
    div.setAttribute("draggable", "true");
    div.setAttribute("ondrag", "handleDragStart");
    div.setAttribute("ondragover", "handleDragover");
    div.setAttribute("ondrop", "DropOut");
    return div;
}

function createRightdiv(jsondatas, jsondata) {
    let li = document.createElement("li");
    console.log(jsondatas);
    // li.dataset[jsondata]=jsondatas[JSON.stringify(jsondata)];
    let ss = JSON.stringify(jsondatas);
    li.setAttribute("data", ss);
    li.dataset.locadpath = jsondatas[jsondata]
    let dv = document.createElement("dv");
    dv.innerText = jsondata;
    let a = document.createElement("a");
    a.appendChild(dv);
    li.appendChild(a);
    li.setAttribute("ondragover", "handleDragover");
    li.setAttribute("ondragover", "handleDragleave");
    li.setAttribute("ondrop", "handleDrope");
    li.setAttribute("tdragover", "yes");
    return li;
}


function dragload(e) {
    drageableds = document.querySelectorAll(".left ul div");
    dropeableds = document.querySelectorAll(".right li");
    if (e===0){
        let drageabled = document.querySelector(".left ul");
        drageabled.innerHTML = '';
    }


    let leftdrag = fetch("http://" + address + ":" + port + "/map/ActionCloude/getCloudePage?" + window.username, {withCredentials: true})
        .then((Response) =>
            Response.json())
        .then(jsondatas => {
            console.log(jsondatas)
            let drageabled = document.querySelector(".left ul");
            drageabled.innerHTML = '';
            leftdiv = {};
            for (var jsondata in jsondatas) {
                var div = createLeftdiv(jsondatas[jsondata]['target'], jsondatas[jsondata]['root']);
                // div.innerText =
                // div.dataset.target = jsondatas[jsondata]['target']
                // div.dataset.absolute =
                // div.setAttribute("draggable", "true");
                // div.setAttribute("ondrag", "handleDragStart");
                // div.setAttribute("ondragover", "handleDragover");
                // div.setAttribute("ondrop", "DropOut");
                drageabled.appendChild(div);
            }
            drageableds = document.querySelectorAll(".left ul div");
        })
        // .catch(e=>window.log(e.message))
    let dropeabled
    let rightdrag = fetch("http://" + address + ":" + port + "/map/ActionCloude/getCloudeTrigger", {withCredentials: true})
        .then((Response) =>
            Response.json())
        .then(jsondatas => {
            dropeabled = document.querySelector(".right ul");
            let dropeabled1 = dropeabled.cloneNode()
            dropeabled.parentNode.appendChild(dropeabled1)
            dropeabled.parentNode.removeChild(dropeabled)
            dropeabled = dropeabled1
            dropeabled.innerHTML = '';
            let a;
            for (var jsondata in jsondatas) {
                var li = document.createElement("li");
                console.log(jsondatas);
                // li.dataset[jsondata]=jsondatas[JSON.stringify(jsondata)];
                // var ss = JSON.stringify(jsondatas);
                // li.setAttribute("data", ss);
                li.dataset.locadpath = jsondatas[jsondata]
                var dv = document.createElement("dv");

                dv.innerText = jsondata;
                a = document.createElement("a");
                a.appendChild(dv);
                li.appendChild(a);
                li.setAttribute('class','rightLiEle')
                li.setAttribute("ondragover", "handleDragover");
                li.setAttribute("ondragover", "handleDragleave");
                li.setAttribute("ondrop", "handleDrope");
                // addtouchdrog(li)
                dropeabled.appendChild(li);
            }

            addDragLongTouchParent(dropeabled,function (e,startEle) {
                    let a= startEle.querySelector('dv')
                    if(a.textContent===startEle.dataset.locadpath){
                        a.textContent=startEle.dataset.locadpath.split('/').pop()
                    }else {
                        a.textContent = startEle.dataset.locadpath
                    }
                },touchmovedrag,touchendRightDel2,["rightLiEle"],
                dragbuffer,"",800)

            addDragLongMouseParent(dropeabled,function (e,startEle) {
                    let a= startEle.querySelector('dv')
                    if(a.textContent===startEle.dataset.locadpath){
                        a.textContent=startEle.dataset.locadpath.split('/').pop()
                    }else {
                        a.textContent = startEle.dataset.locadpath
                    }
                },touchmovedrag,touchendRightDel2,["rightLiEle"],
                dragbuffer,"",800)
            console.log(jsondatas);}
        )
    Promise.all([leftdrag, rightdrag])
        .then(() => {
            return fetch("http://" + address + ":" + port + "/map/ActionCloude/getMapTend?" + window.username,
                {withCredentials: true})
                .then((Response) =>
                    Response.json())
                .then((json) => {
                    console.log(json);
                    return json;
                }).then(jsondatas => {
                    let nodes = dropeabled.childNodes;
                    let div;
                    for (let node of nodes) {
                        let abp = jsondatas[node.dataset.locadpath]
                        if (abp != null && abp !== "[]" && abp !== undefined) {
                            for (let n in abp) {
                                div = leftdiv[abp[n]];
                                if (div !== null && div !== undefined) {
                                    let clonenode = div.cloneNode(true);
                                    clonenode.setAttribute("drop", "")
                                    clonenode.setAttribute("class", "dropright")
                                    clonenode.addEventListener('dragstart', handleDragStart);
                                    // element1.removeEventListene('drop', handleDrope)
                                    clonenode.addEventListener('drop', function (event) {
                                        event.stopPropagation();
                                    });
                                    addtouchdragRight(clonenode)
                                    node.appendChild(clonenode)
                                }
                            }
                        }
                    }
                }).catch(error => {
                    console.error('There was a problem with your fetch operation:', error);
                    throw error; // 重新抛出错误，以便在Promise.all中捕获
                });
        }).then(() => {
            dropeableds = document.querySelectorAll(".right li");
            // dropdv = document.querySelectorAll(".right li dv");
            uploadbutton = document.querySelector("#uploadbutton");
            console.log("uploadbutton");
            // uploadbutton.click = uploadTendMap;
            adddrag();
            let showfileelement = document.getElementById('showfile')
            addshowfile(showfileelement);
            let viewfileelement = document.getElementById('viewFileCon')
            addViewfile(viewfileelement);
            let li = document.createElement("li");
            li.setAttribute('class','del')
            let dv = document.createElement("addBtn");
            dv.setAttribute("class", "addBtn");
            let a=document.createElement('a')
            let text=document.createElement('dv')
            a.appendChild(text)
            li.appendChild(dv);
            li.appendChild(a)
            // li.setAttribute('class','rightLiEle')

            if (dropeabled.querySelector(".addBtn")==null||dropeabled.querySelector(".addBtn")===undefined){
                dropeabled.appendChild(li);
                let addfile = SelectPathFun()
                li.addEventListener("click", addfile);
            }
            console.log('第三个请求的');
        getMode()
        if (e!==0){
            scrollToEle(document.getElementById('drag'),0)
        }
        }).catch(error => {
            // 捕获所有请求中可能发生的错误
            console.error('Stack Trace:', error.stack);
        });

    // uploadbutton = document.querySelector(".uploadbutton");
    // console.log("uploadbutton");
    // uploadbutton.onclick = uploadTendMap;
    // adddrag();
    // let showfileelement = document.getElementById('showfile')
    // adddshowfile(showfileelement);
    // var li = document.createElement("li");
    // var dv = document.createElement("addBtn");
    // dv.setAttribute("class", "addBtn");
    // li.appendChild(dv);
    // dropeabled.appendChild(li);
    // li.addEventListener("click",addfile);
}

// dragload();
// fetch("http://" + address + ":" + port + "/map/ActionCloude/getCloudeTrigger", {withCredentials: true})
//     .then((Response) =>
//         Response.json())
//     .then(jsondatas => {
//         dropeabled = document.querySelector(".right ul");
//         dropeabled.innerHTML = '';
//         let a;
//         for (var jsondata in jsondatas) {
//             var li = document.createElement("li");
//             console.log(jsondatas);
//             // li.dataset[jsondata]=jsondatas[JSON.stringify(jsondata)];
//             var ss = JSON.stringify(jsondatas);
//             li.setAttribute("data", ss);
//             li.dataset.locadpath = jsondatas[jsondata]
//             var dv = document.createElement("dv");
//
//             dv.innerText = jsondata;
//             a = document.createElement("a");
//             a.appendChild(dv);
//             li.appendChild(a);
//             li.setAttribute("ondragover", "handleDragover");
//             li.setAttribute("ondragover", "handleDragleave");
//             li.setAttribute("ondrop", "handleDrope");
//             addtouchdrog(li)
//             dropeabled.appendChild(li);
//         }
//         console.log(jsondatas);}
//     )()

let dropdv = document.querySelectorAll(".right li dv");
let uploadbutton = document.querySelector("#uploadbutton");
console.log("uploadbutton");

function uploadTendMap() {
    dropeableds = document.querySelectorAll(".right li");
    var listmap = []
    var i1 = 0;
    dropeableds.forEach((dropeabled) => {
        var map = {}
        var list = []
        var i2 = 0;
        var keydata = dropeabled.dataset.locadpath;
        let nodes;
        if (keydata !== null && keydata !== undefined) {
            nodes = dropeabled.childNodes
            nodes.forEach((node) => {
                tn = node.tagName
                if (tn === 'DIV') {
                    // var datacp=div.getData['cloudePage'];
                    var target = node.dataset.target
                    var absolute = node.dataset.absolute
                    list[i2] = absolute + target;
                    i2 = i2 + 1;
                }
            });
            console.log('type of: ' + keydata.constructor.name);
            // map.set(keydata,list)
            map[keydata] = list;
            // listmap.push(map)
            listmap[i1] = map;
            i1 = i1 + 1;
        }
    })
    var jsondatas = JSON.stringify(listmap)
    fetch("http://" + address + ":" + port + "/map/Cloude/TendMap?" + window.username, {
        method: 'post',
        body: jsondatas,
    }).catch(error => {
        console.error(error)})
}


// let showfiles = document.getElementsByClassName(".showfile");
// showfiles.forEach((showfile)=>{
// })

function addshowfile(element) {
    element.removeEventListener("dragover", handleDragover);
    // element.addEventListener("dragover", handleDragleave);
    element.removeEventListener("drop", getCloudePathList);
    element.addEventListener("dragover", handleDragover);
    // element.addEventListener("dragover", handleDragleave);
    element.addEventListener("drop", getCloudePathList);
}
function addViewfile(element) {
    element.removeEventListener("dragover", handleDragover);
    // element.addEventListener("dragover", handleDragleave);
    element.removeEventListener("drop", getCloudePathList);
    element.addEventListener("dragover", handleDragover);
    // element.addEventListener("dragover", handleDragleave);
    element.addEventListener("drop", viewFile);
}

let dataShowFile = {}

function getCloudePathList(e) {
    let ele = e.target
    while (ele.className !== 'showfile') {
        if (ele.parentNode === null) {
            ele = null;
            break;
        }
        ele = ele.parentNode;
    }
    let dragelement = window.drapElement
    let target = dragelement.dataset.target;
    let absolute = dragelement.dataset.absolute;
    let abpath = absolute + target
    if (dataShowFile[abpath] !== null || dataShowFile[abpath] !== undefined) {
        dataShowFile = {}
        let obj = {}
        obj['ab'] = abpath
        obj['name'] = window.username
        // let abs=ab.split('/')
        obj['path'] = target
        let param = JSON.stringify(obj)
        fetch("http://" + address + ":" + port + "/map/show/cPathList1?" + param)
        .then((Response) => Response.json())
        .then((json) => {
            // let keys = Object.keys(json);
            let divcontains = document.createElement("div");
            divcontains.setAttribute('class', 'filecontain');

            let filelist = document.createElement("div");
            filelist.setAttribute('class', 'filelist');
            let del = document.createElement("div");
            del.setAttribute('class', 'delfileContarin')
            del.addEventListener('click', delbutton)
            filelist.appendChild(del)
            // let div = document.createElement("div")
            let ss;
            let divn;
            for (let key in json) {
                divn = document.createElement("div")
                if (json[key] === 'f') {
                    divn.setAttribute('class', 'file')
                    divn.setAttribute('filename', key)
                    divn.addEventListener('click', openCloudeFile)
                } else {
                    divn.setAttribute('class', 'path')
                    divn.setAttribute('pathname', key)
                    divn.addEventListener('click', getCloudePathListson)
                }
                ss = key.split('/')
                divn.innerText = ss[ss.length - 1]
                divn.dataset.abpath = key
                filelist.appendChild(divn)
            }
            divcontains.dataset.ab = abpath
            divcontains.dataset.name = window.username
            divcontains.appendChild(filelist)
            let showfiles = document.getElementsByClassName('showfile');
            showfiles.item(0).appendChild(divcontains)
            filelist.dataset.sn = 0;
            dataShowFile[0] = target;

            addDragTouchParent(filelist,touchEvenFun ,touchMoveEvenFun,touchLongFun,['path','file'],
                document.getElementById('dragbuffer'),
                "",800)
            addDragMouseParent(filelist,mouseEvenFun,mouseMoveEvenFun,mouseLongFun,['path','file'],
                document.getElementById('dragbuffer'),
                "",800)

            // addLongTouchParent(filelist,,['path','file'],1000)
        })
    }
}

function getCloudePathListson(e) {
    let ele = this.parentNode
    let data = {}
    data['ab'] = ele.parentNode.dataset.ab;
    data['name'] = ele.parentNode.dataset.name;
    let abs = this.dataset.abpath.split('/');
    data['path'] = abs[abs.length - 1];
    let param = JSON.stringify(data);
    loadstart();
    fetch("http://" + address + ":" + port + "/map/show/cPathList", {
        method: 'post',
        body: param
    })
        .then((Response) => Response.json())
        .then((jsons) => {
            // let keys = Object.keys(json);
            let filelist = document.createElement("div")
            filelist.setAttribute('class', 'filelist')
            filelist.dataset.p=this.dataset.abpath
            // let div = document.createElement("div")
            let ss;
            let divn = document.createElement("div")
            divn.addEventListener("click", removeParentNode)
            divn.setAttribute('class', 'uplay')
            divn.innerText = '...'
            filelist.appendChild(divn)
            // div.appendChild(divn)
            // let jsons = JSON.parse(json)
            for (let key in jsons) {
                divn = document.createElement("div")
                if (jsons[key] === 'f') {
                    divn.setAttribute('class', 'file')
                    divn.setAttribute('filename', jsons[key])
                    divn.addEventListener('click', openCloudeFile)
                } else {
                    divn.setAttribute('class', 'path')
                    divn.setAttribute('pathname', jsons[key])
                    divn.addEventListener('click', getCloudePathListson)
                }
                ss = key.split('/')
                divn.addEventListener("mouseover", function () {
                    divn.innerText = key
                })
                divn.addEventListener("mouseout", function () {
                    divn.innerText = ss[ss.length - 1]
                })
                divn.innerText = ss[ss.length - 1]
                divn.dataset.abpath = key
                // div.appendChild(divn)
                filelist.appendChild(divn)
            }
            ele.parentNode.appendChild(filelist)
            let dl = Object.keys(dataShowFile).length
            dl++;
            filelist.dataset.sn = dl;

            addDragTouchParent(filelist,touchEvenFun,touchMoveEvenFun,touchLongFun,['path','file'],
                document.getElementById('dragbuffer'),
                "",1500)
            addDragMouseParent(filelist,mouseEvenFun,mouseMoveEvenFun,mouseLongFun,['path','file'],
                document.getElementById('dragbuffer'),
                "",1500)
            loadstart();
        }).then(() => {
        loadstop()
    }).catch(e => loadstop())
}

function delElement(e) {
    let ele = e.parentNode
    this.parentNode.removeChild(ele)
}

// function openFile(e) {
//     let filename = this.getAttribute('filename')
//     window.open("http://" + address + ":" + port + "/map/ActionCloude/getFile?" + filename)
// }
function centerElement(element) {
    if (!element) return;

    // 获取元素自身尺寸
    const width = element.offsetWidth;
    const height = element.offsetHeight;

    // 计算视口中心坐标
    const viewportWidth = window.innerWidth;
    const viewportHeight = window.innerHeight;
    const centerX = viewportWidth / 2;
    const centerY = viewportHeight / 2;

    // 计算需要的translate值（考虑元素自身尺寸的一半）
    const translateX = centerX - width / 2 -10;
    const translateY = centerY - height / 2 -10;

    // 应用样式
    element.style.transform = `translate(${translateX}px, ${translateY}px)`;

    // 同时确保元素在视口范围内（根据你的max-width/max-height设置）
    element.style.maxWidth = '96vw';
    element.style.maxHeight = '94vh';
    element.style.width = 'auto';
    element.style.height = 'auto';
}


let selectFileEle;

function openCloudeFile(e) {
    let filename = this.getAttribute('filename')
    selectFileEle=this
    filename = this.dataset.abpath
    let data = {}
    data['name'] = window.username;
    data['file'] = filename
    let d = JSON.stringify(data)
    let filel=filename.toLowerCase()
    const picExtensions = [".jpg", ".jpeg", ".png", ".gif", ".webp", '.avif', '.svg', '.bmp', '.ico','.heic'];
    const videoExtensions = [".mp4",".m4v",".webm", ".m3u8",".ts",  ".m4s" ];
    const txtExtensions = [".txt", ".json", ".xml", ".csv", ".md", ".html", ".css", ".js", ".log", ".yaml", ".yml"];

    if (picExtensions.some(ext => filel.endsWith(ext))) {
        loadstart()
        let ele=this
        addDragLoader(ele)
        fetch("http://" + address + ":" + port + "/map/ActionCloude/getCloudeFile?" + d
            // , {
            // method: 'post',
            // body: d,
            // headers: {'Content-Type': 'application/json',},}
        ).then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.blob(); // 获取Blob对象
        }).then(async blob => {
            let imageContainer = document.getElementById('showPicC');
            clearEleChildren(imageContainer)
            // let showb = imageContainer.querySelector('#showPib')
            // if (showb === null || showb === undefined) {
                let showb = document.createElement("div")
                showb.setAttribute("id", 'showPib')
                imageContainer.appendChild(showb);
            // }

            // 创建一个对象URL指向Blob对象
            let imageUrl = URL.createObjectURL(blob);
            if (filel.toLowerCase().includes('.heic')) {
                imageUrl = await convertHEICtoJPEG(blob)
            }
            // 创建一个img元素并设置其src属性为对象URL
            let imgElement = document.createElement('img');
            imgElement.src = imageUrl;
            imgElement.setAttribute('class', 'picShow');
            imgElement.dataset.path = filename
            imgElement.addEventListener('touchstart', setTouchLongTimeCloude)
            cancelTouchRS()
            setTouchRS(imgElement)
            // setCloudTouchRS(imgElement)
            let closeElement = document.createElement('div');
            closeElement.setAttribute('class', 'showPicClose')
            closeElement.addEventListener('click', closePicNode)
            closeElement.addEventListener('click', cancelTouchRS)
            // let imageContainer = document.createElement('div');
            // imageContainer.setAttribute('class','showPic')

            // imageContainer0.appendChild(imageContainer);
            // 将img元素添加到div中
            imageContainer.appendChild(imgElement);
            imageContainer.appendChild(closeElement);

            imageContainer.style.display = 'flex';
            showb.style.display = 'flex'
            return imgElement;
        }).then(imageContainer => {
            // let imgElement = document.querySelector('#showPicC .picShow')
            // centerElement(imgElement)
            // let width=imageContainer.offsetWidth;
            // let height=imageContainer.offsetHeight;
            // let size=width/window.screenTop;
            // imageContainer.style.width=window.screenTop*0.8;
            // imageContainer.style.height=(height/size)*0.8;
            loadstop()
            removeDragLoader(ele)
            imageContainer.onload = () => {
                requestAnimationFrame(() => {
                    let imgElement = document.querySelector('#showPicC .picShow')
                    centerElement(imgElement)
                });
            };
        }).catch(error => {
            console.error('There was a problem with the fetch operation:', error);
            loadstop()
        });
        return null;
    } else if(videoExtensions.some(ext => filel.endsWith(ext))) {
            let imageContainer = document.getElementById('showPicC');
            clearEleChildren(imageContainer)
            // let showb = imageContainer.querySelector('#showPib')
            // if (showb === null || showb === undefined) {
                let showb = document.createElement("div")
                showb.setAttribute("id", 'showPib')
                imageContainer.appendChild(showb);
            // }
            // 创建一个对象URL指向Blob对象
            // 创建一个img元素并设置其src属性为对象URL
            let imgElement = document.createElement('video');
            imgElement.setAttribute('id', 'videoPlayer');
            imgElement.setAttribute('class', 'picShow');
            imgElement.addEventListener('touchstart', setTouchLongTimeCloude)
            imgElement.dataset.path = filename

            let closeElement = document.createElement('div');
            closeElement.setAttribute('class', 'showPicClose')
            closeElement.addEventListener('click', closePicNode)
            closeElement.addEventListener('click', cancelTouchRS)

            // 将img元素添加到div中
            imageContainer.appendChild(imgElement);
            imageContainer.appendChild(closeElement);
            imageContainer.style.display = 'flex';
            showb.style.display = 'flex'

            // 创建MediaSource对象
            const mediaSource = new MediaSource();
            const videoPlayer = imgElement;
            let videoUrl="http://" + address + ":" + port + "/map/ActionCloude/getVideo"
            videoPlayer.src = URL.createObjectURL(mediaSource);
            mediaSource.addEventListener('sourceopen', function() {
                // const sourceBuffer = mediaSource.addSourceBuffer('video/mp4; codecs="avc1.42E01E, mp4a.40.2"');
                const sourceBuffer = mediaSource.addSourceBuffer('video/mp4');
                // 初始化变量
                let startByte = 0;
                // 函数，用于加载下一个视频块
                async function loadNextChunk() {
                    await fetchNextVideoChunk(videoUrl, filename, startByte).then(async blob => {
                        // const reader = new FileReader();

                        // reader.onload = function () {
                        //     const arrayBuffer = reader.result;
                        //     // 此时的arrayBuffer就是转换好的ArrayBuffer类型数据，可以传递给appendBuffer方法了
                        //     if (sourceBuffer && sourceBuffer.readyState === 'open') {
                        //         sourceBuffer.appendBuffer(arrayBuffer);
                        //     } else {
                        //         console.error("SourceBuffer is not in a valid state for appending data");
                        //     }
                        // };
                        // reader.readAsArrayBuffer(blob);
                        const arrayBuffer = await blob.arrayBuffer();
                        // await arrayBuffer.then( arrayBuffer => {
                            const uint8Array = new Uint8Array(arrayBuffer);

                            // 当所有数据都已加载完成时，结束当前缓冲区
                            sourceBuffer.addEventListener('updateend', () => {
                                if (!sourceBuffer.updating) {
                                    console.log('Append complete');

                                    // 检查 buffered 属性
                                    const buffered = sourceBuffer.buffered;
                                    console.log('Buffered ranges:', buffered);
                                    if (buffered.length > 0) {
                                        console.log('First buffered range start:', buffered.start(0));
                                        console.log('First buffered range end:', buffered.end(0));
                                    } else {
                                        console.log('No buffered data');
                                    }
                                    // 开始播放视频
                                    videoPlayer.play().catch(error => {
                                        console.error('Error playing video:', error);
                                    });
                                }

                                if (!sourceBuffer.updating && sourceBuffer.buffered.length > 0) {
                                    console.log('All data has been loaded.');
                                }
                            });
                            sourceBuffer.appendBuffer(uint8Array);
                        // });
                        // 将媒体数据推送到 SourceBuffer
                        // sourceBuffer.appendBuffer(blob);
                        startByte += blob.size; // 更新下一个块的起始字节
                    }).catch(error => {
                        console.error('Error loading video chunk:', error);
                    });
                }
                // 监听缓冲区更新事件，并在缓冲区更新完成后加载下一个块
                sourceBuffer.addEventListener('updateend', async function() {
                    if (!sourceBuffer.updating && mediaSource.readyState === 'open') {
                        await loadNextChunk();
                    }
                });
                    // 开始加载第一个视频块
                    loadNextChunk();
                });

            // 监听 sourceended 事件
            mediaSource.addEventListener('sourceended', function() {
                console.log('MediaSource ended');
            });

// 监听 sourceclose 事件
            mediaSource.addEventListener('sourceclose', function() {
                console.log('MediaSource closed');
            });
        }else if(txtExtensions.some(ext => filel.endsWith(ext))){
            loadstart()
            let ele=this
            addDragLoader(ele)
            fetch("http://" + address + ":" + port + "/map/ActionCloude/getCloudeFile?" + d
                // , {
                // method: 'post',
                // body: d,
                // headers: {'Content-Type': 'application/json',},}
            ).then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text(); // 获取Blob对象
            }).then(async text => {
                let imageContainer = document.getElementById('showPicC');
                clearEleChildren(imageContainer)
                let showb = imageContainer.querySelector('#showPib')
                if (showb === null || showb === undefined) {
                    showb = document.createElement("div")
                    showb.setAttribute("id", 'showPib')
                    imageContainer.appendChild(showb);
                }

                let imgElement = document.createElement('text');
                imgElement.setAttribute('class', 'picShow');
                imgElement.style.width='98vw'
                imgElement.style.height='98vh'
                imgElement.style.backgroundColor='white'
                imgElement.style.marginTop='3vh';
                imgElement.dataset.path = filename
                imgElement.textContent =text
                imgElement.addEventListener('touchstart', setTouchLongTimeCloude)
                setTouchRS(imgElement)

                let closeElement = document.createElement('div');
                closeElement.setAttribute('class', 'showPicClose')
                closeElement.addEventListener('click', closePicNode)
                closeElement.addEventListener('click', cancelTouchRS)
                // let imageContainer = document.createElement('div');
                // imageContainer.setAttribute('class','showPic')

                // imageContainer0.appendChild(imageContainer);
                // 将img元素添加到div中
                imageContainer.appendChild(imgElement);
                imageContainer.appendChild(closeElement);

                imageContainer.style.display = 'flex';
                showb.style.display = 'flex'
                return imgElement;
                // downloadFile("http://" + address + ":" + port + "/map/ActionCloude/getCloudeFile?" + d, filename)
            }).then(imageContainer => {
                loadstop()
                removeDragLoader(ele)
            }).catch(error => {
                console.error('There was a problem with the fetch operation:', error);
                removeDragLoader(ele)
                loadstop()
            });
        }else {
            loadstart()
            let ele=this
            addDragLoader(ele)
            fetch("http://" + address + ":" + port + "/map/ActionCloude/getCloudeFile?" + d
                // , {
                // method: 'post',
                // body: d,
                // headers: {'Content-Type': 'application/json',},}
            ).then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.blob(); // 获取Blob对象
            }).then(async blob => {
                let imageContainer = document.getElementById('showPicC');
                clearEleChildren(imageContainer)
                let showb = imageContainer.querySelector('#showPib')
                if (showb === null || showb === undefined) {
                    showb = document.createElement("div")
                    showb.setAttribute("id", 'showPib')
                    imageContainer.appendChild(showb);
                }

                let imgElement = document.createElement('img');
                imgElement.setAttribute('class', 'picShow');
                imgElement.style.width='50vw'
                imgElement.style.height='50vh'
                imgElement.style.backgroundColor='white'
                imgElement.style.left= '25vw';
                imgElement.dataset.path = filename
                imgElement.addEventListener('touchstart', setTouchLongTimeCloude)
                setTouchRS(imgElement)

                let closeElement = document.createElement('div');
                closeElement.setAttribute('class', 'showPicClose')
                closeElement.addEventListener('click', closePicNode)
                closeElement.addEventListener('click', cancelTouchRS)
                // let imageContainer = document.createElement('div');
                // imageContainer.setAttribute('class','showPic')

                // imageContainer0.appendChild(imageContainer);
                // 将img元素添加到div中
                imageContainer.appendChild(imgElement);
                imageContainer.appendChild(closeElement);

                const width = imgElement.offsetWidth;
                const height = imgElement.offsetHeight;

                // 计算视口中心坐标
                const viewportWidth = window.innerWidth;
                const viewportHeight = window.innerHeight;
                const centerX = viewportWidth / 2;
                const centerY = viewportHeight / 2;

                // 计算需要的translate值（考虑元素自身尺寸的一半）
                const translateX = centerX - width / 2 -10;
                const translateY = centerY - height / 2 -10;

                // 应用样式
                imgElement.style.transform = `translate(${translateX}px, ${translateY}px)`;
                // 同时确保元素在视口范围内（根据你的max-width/max-height设置）
                imgElement.style.maxWidth = '96vw';
                imgElement.style.maxHeight = '94vh';
                imageContainer.style.display = 'flex';
                showb.style.display = 'flex'
                imgElement.style.left= null;

                return imgElement;
           // downloadFile("http://" + address + ":" + port + "/map/ActionCloude/getCloudeFile?" + d, filename)
        }).then(imageContainer => {
                loadstop()
                removeDragLoader(ele)
            }).catch(error => {
                console.error('There was a problem with the fetch operation:', error);
                removeDragLoader(ele)
                loadstop()
            });
        }
    // window.open("http://" + address + ":" + port + "/map/ActionCloude/getCloudeFile/&" + window.username,{
    //     method: 'post',
    //     body: d,
    //     headers: {'Content-Type': 'application/json',},
    // })
}
async function convertHEICtoJPEG(file) {
    // const arrayBuffer = await file.arrayBuffer();
    const jpegData = await window.heic2any({ blob:file,  // 将heic转换成一个buffer数组的图片
        toType: 'image/jpg', //要转化成具体的图片格式，可以是png/gif
        quality: 0.5   // 图片的质量，参数在0-1之间
    });
    // const arrayBuffer = await file.arrayBuffer();
    // const options = {
    //     toType: 'image/jpeg',
    //     quality: 0.8
    // };
    //
    // const jpegData = await heic2any(options, arrayBuffer);
    const blob = new Blob([jpegData], { type: 'image/jpeg' });
    return URL.createObjectURL(blob);
}
function setTouchLongTimeCloude(e) {
    let eleImg=e.target
    let path=eleImg.dataset.path
    let timer= setTimeout(function (){
        let show = eleImg.parentNode
        // let show = eleImg.parentNode.querySelector("#showPib")
        let so=show.querySelector('.showC')
        if (so==null){
            let showC=document.createElement('div')
            showC.setAttribute('class','showCC')
            let showCO=document.createElement('div')
            showCO.setAttribute('class','showCO')
            let but=document.createElement('div')
            but.setAttribute('class','listbutton')
            but.textContent="下载"
            but.dataset.path=path
            but.addEventListener("click",downFile)

            let save=document.createElement('div')
            save.setAttribute('class','listbutton')
            save.textContent="保存"
            save.dataset.path=path
            save.addEventListener("click",saveFile)
            // but.addEventListener("touchmove",function () {
            //     rem()
            // })
            but.addEventListener("mousemove", function () {
                rem()
            })
            show.appendChild(showC)
            showC.appendChild(showCO)
            showCO.appendChild(but)
            showCO.appendChild(save)
            show.onclick=function (){
                show.removeChild(showC)
            }
        }
    }, 1100);
    let rem=function (event) {
        clearTimeout(timer)
        touchMoved=false;
    }
    eleImg.addEventListener("touchend",rem)
    eleImg.addEventListener("touchmove",rem)
    eleImg.addEventListener("mouseup",rem)
    eleImg.addEventListener("mousemove",rem)
}
function downloadFile(url, filename) {
    const a = document.createElement('a');
    a.href = url;
    a.download = filename || 'download';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
}

function removeParentNode(e) {
    e.stopPropagation()
    this.parentNode.parentNode.removeChild(this.parentNode)
    // let showb=document.querySelector('#showPib')
    // showb.style.display='none'
}
function closePicNode(e) {
    e.stopPropagation()
    clearEleChildren(this.parentNode)
    let showb=document.querySelector('#showPib')
    showb.style.display='none'
}


function getLocalPathList(e) {
    fetch("http://" + address + ":" + port + "/map/ActionCloude/getFile")
}


adddrag();

function adddrag() {
    drageableds.forEach((item) => {
        item.addEventListener('dragstart', handleDragStart)
        // item.addEventListener('touchstart', tochHandleDragStart)
        // item.setAttribute("draggable", "true");
        // item.setAttribute("ondrag", "handleDragStart");
        addtouchdrag(item)
        item.addEventListener("dragover", handleDragover);
        item.addEventListener("drop", DropOut);
        // item.addEventListener("click", isShowFile);
    })
    dropeableds.forEach((item) => {
        item.addEventListener("dragover", handleDragover);
        item.addEventListener("dragover", handleDragleave);
        item.addEventListener("drop", handleDrope);
        // addtouchdrog(item)
    })
    dropdv.forEach((item) => {
        item.addEventListener("dragover", handleDragover);
        item.addEventListener("dragover", handleDragleave);
        item.addEventListener("drop", unDrope);
    })
}

// drageableds.addEventListener("dragstart", handleDragStart);
function handleDragStart(e) {
    window.drapElement = e.target;
    draggableElement=e.target
    e.dataTransfer.effectAllowed = "copy";
    e.dataTransfer.setData("text/plain", e.target.id);
}

function handleDragover(e) {
    e.preventDefault();
    e.target.classList.add("dragover");
    //dropeableds.classList.add("dragover");
}
//dropeableds.addEventListener("dragover", handleDragleave);
function handleDragleave(e) {
    e.preventDefault();
    e.target.classList.remove("dragover");
    //dropeableds.classList.remove("dragover");
}

//dropeableds.addEventListener("drop", handleDrope);
function handleDrope(e) {
    e.stopPropagation();
    // e.preventDefault();
    console.log(item);
    // if (e.target === dropeableds) { // 检查是否在放置区域上放下元素
    //   let dropRect = e.target.getBoundingClientRect(); // 获取放置区域的位置
    //   let newLeft = dropRect.left - originalPosition.left; // 计算新的位置
    //   let newTop = dropRect.top - originalPosition.top; // 计算新的位置
    //   drapElement.style.left = newLeft + "px"; // 设置新位置
    //   drapElement.style.top = newTop + "px"; // 设置新位置
    // }
    e.dataTransfer.effectAllowed = "copy";
    // let draggetId = e.dataTransfer.getData("text/plain");
    let element0 = window.drapElement;
    let element1 = element0.cloneNode(true);
    element1.addEventListener('dragstart', handleDragStart);
    element1.setAttribute('class','dropright')
    element1.addEventListener('drop', function (event) {
        event.stopPropagation();
    });
    this.appendChild(element1);
    this.classList.add("drop");

    window.drapElement = null;
    uploadTendMap();
}

function unDrope(e) {
    e.stopPropagation();
    e.dataTransfer.effectAllowed = "copy";
    let draggetId = e.dataTransfer.getData("text/plain");
    element0 = drapElement;
    drapElement = null;
    element1 = element0.cloneNode(true);
    element1.setAttribute("drop", "")
    element1.addEventListener('dragstart', handleDragStart);
    // element1.removeEventListene('drop', handleDrope)
    element1.addEventListener('drop', function (event) {
        event.stopPropagation();
    });
    e.target.parentNode.appendChild(element1);
    e.target.classList.add("drop");

    uploadTendMap();
}

function DropOut(e) {
    e.preventDefault();
    // e.dataTransfer.effectAllowed = "move";
    ter = e.dataTransfer;
    let draggetId = ter.getData("text/plain");
    var el1 = drapElement;
    if (el1.parentNode.tagName !== 'UL') {
        drapElement = null;
        var el2 = el1.parentNode;
        console.log(el1);
        console.log(el2);
        el2.removeChild(el1);
    }
    // dropeableds.parentNode.removeChild(ell);
    // dropeableds.classList.add("drop");
    uploadTendMap();
}

// listmap.forEach((l1)=>{
//   for (let  entry of l1.entries()) {
//     console.log('' +entry.constructor.name);
//   }
//   l1.forEach((v,k)=>{
//     v.forEach((l2)=>{
//       console.log('k: ',k.constructor.name);
//       console.log('k: ',l2.constructor.name);
//     })
//   })
// })

let opertaAreasid = document.getElementsByClassName("opertaArea")[0]
let showAreasid = document.getElementsByClassName("showfile")[0]
let dragContainer = document.getElementsByClassName("dragContainer")[0]
let opertaContainer = document.getElementById("cloudeOpertor")

let modeButton = document.querySelector("#modebutton")
modeButton.addEventListener('click', setMode)
enableLongPress(modeButton,function (e){
    let str = modeButton.textContent
    if (str === 'Monitor'){
        let border = createInputBord(modeButton)
        let mon = function (e) {
            let data = {}
            let monFn = border.querySelector('#monFn')
            let monFz = border.querySelector('#monFz')
            data['fn'] = monFn.value
            data['fz'] = monFz.value
            let jsondatas = JSON.stringify(data)
            fetch("http://" + address + ":" + port + "/map/Cloude/setMon", {
                method: 'post',
                body: jsondatas,
            }).then(response=>response.ok).then((ok)=>{
                if(ok){
                    showText1("修改成功")
                }
            }).catch(e=>{showText1("修改失败")})
        }
        createInput(border,mon,mon,'文件数',"monFn")
        createInput(border,mon,mon,'存储额',"monFz")
        createInput(border,mon,mon)
    } else if (str ===  'Consist'){
        s="Consist";
    } else if (str ===  'Server'){
        s="Server";
    } else if (str ===  'Browse'){
        s="Browse";
    }
},function (e) {
    // e.preventDefault()
    // e.stopPropagation()
})

function getMode(e) {
    // e.stopPropagation(); // 阻止事件继续向下传递
    let str = modeButton.textContent
    fetch("http://" + address + ":" + port + "/map/Cloude/getCloudeMode?" + str)
        .then((Response) => Response.text())
        .then(text => {
            // modeButton.textContent = text;
            modeButton.removeChild(modeButton.firstChild)
            modeButton.insertAdjacentText("afterbegin", text);
        })
        .catch(error => console.error(error))
}

function setMode(e) {
    e.stopPropagation(); // 阻止事件继续向下传递
    let str = modeButton.textContent
    fetch("http://" + address + ":" + port + "/map/Cloude/changeMode?" + str)
        .then((Response) => Response.text())
        .then(text => {
            // modeButton.textContent = text;
            modeButton.removeChild(modeButton.firstChild)
            modeButton.insertAdjacentText("afterbegin", text);
        })
        .catch(error => console.error(error))
}
document.querySelector('#reqCloude').addEventListener('click',reqCloude)
function reqCloude() {
    loadstart()
    fetch("http://"+address+":" +port+"/map/Cloude/reqCloude?"+window.username)
        .then((Response)=> {Response.text()
        }).then(text=>{
        console.log(Response);
        dragload()
        adddrag();
        loadstop()
        if (cloudstate){
            cloudeOn()
        }
    }).catch(error => loadstop())
}

document.querySelector('#reqCloudeMirror').addEventListener('click',reqCloudeMirror)
function reqCloudeMirror() {
    loadstart()
    fetch("http://"+address+":" +port+"/map/Cloude/reqCloudeMirror?"+window.username)
        .then((Response)=> {console.log(Response);loadstop()})
        .then((Response)=> {Response.text()
        }).then(text=>{
        console.log(Response);
        dragload()
        adddrag();
        loadstop()
    }).catch(error => loadstop())
}

document.querySelector('#immbutton').addEventListener('click',cloudeImmediate)
function cloudeImmediate() {
    fetch("http://"+address+":" +port+"/map/ActionCloude/immediate")
        .then((Response)=> Response.text())
        .then((text)=> {
            let body=document.querySelector(".create")
            let ssss=document.createElement("div")
            ssss.setAttribute("class","textbox1");
            ssss.innerText=text
            body.appendChild(ssss);
            window.setTimeout(function () {
                body.removeChild(ssss);
            },1500)
            console.log(Response)})
}
$('#nodebufferTime').load('htmls/t4.html');
let timeFuntion
document.querySelector('#tranTimebutton').addEventListener('click',cloudeTime)
function cloudeTime(e) {
    if (e.target!==this){
        return
    }
    document.querySelector('#nodebufferTime').style.display='initial'
    timeFuntion=timetran
}
function timetran(time) {
    let data={}
    data['user']=window.username
    data['time']=time
    data=JSON.stringify(data)
    fetch("http://"+address+":" +port+"/map/ActionCloude/CloudeTime?"+data)
        .then((Response)=> Response.text())
        .then((text)=> {
            let body=document.querySelector(".create")
            let ssss=document.createElement("div")
            ssss.setAttribute("class","textbox1");
            ssss.innerText=text
            body.appendChild(ssss);
            window.setTimeout(function () {
                body.removeChild(ssss);
            },1500)
            console.log(Response)})
}

document.querySelector('#autoMap').addEventListener('click', createAutoMap)
document.querySelector('#autoMap1').addEventListener('click', createAutoMap1)

function createAutoMap() {
    loadstart()
    fetch("http://" + address + ":" + port + "/map/ActionCloude/autoMap?"+window.username)
        .then((Response) => {
            Response.text()
        }).then(text => {
        console.log(Response);
        dragload()
        adddrag();
        loadstop()
        if (cloudstate) {
            cloudeOn()
        }
    }).catch(error => loadstop())
}
function createAutoMap1() {
    loadstart()
    fetch("http://" + address + ":" + port + "/map/ActionCloude/autoMap1")
        .then((Response) => {
            Response.text()
        }).then(text => {
        console.log(Response);
        dragload()
        adddrag();
        loadstop()
        if (cloudstate) {
            cloudeOn()
        }
    }).catch(error => loadstop())
}



function addtouchdrag(e) {
    e.addEventListener('touchstart', touchstartdrag);
    e.addEventListener('touchmove', touchmovedrag);
    e.addEventListener('touchend', touchenddrag);
}

function addtouchdrog(e) {
    e.addEventListener('touchstart', touchstartdrag);
    e.addEventListener('touchmove', touchmovedrag);
    e.addEventListener('touchend', touchendRightDel);
}

function addtouchdragRight(e) {
    e.addEventListener('touchstart', touchstartdrag);
    e.addEventListener('touchmove', touchmovedrag);
    e.addEventListener('touchend', touchendRight);
}
let draggableElement;
var isDragging = false;
var startX, startY;
let dragbuffer=document.getElementById('dragbuffer');
let touchX,touchY
var yPosition=0
var xPosition=0
let selectdragElement;
let styleProperties = ['font-size', 'background-color', 'padding', 'border-radius'];
function touchstartdrag(e) {
    // 阻止默认的触摸行为，比如滚动
    e.preventDefault();
    e.stopPropagation()
    console.log("touchstartdrag")
    // 记录开始拖动时的位置
    startX = e.touches[0].clientX;
    startY = e.touches[0].clientY;

    let element=e.target
    while (element!==this){
        if (element.nodeType === 1){
            element=element.parentNode
        }else {
            return
        }
    }
    draggableElement=element.cloneNode(true)
    selectdragElement=element;
    draggableElement.setAttribute('class','dragleftbuffer')
    let style=window.getComputedStyle(selectdragElement)
    styleProperties.forEach(function(styleName) {
        var styleValue = style.getPropertyValue(styleName);
        if (styleValue) {
            draggableElement.style[styleName] = styleValue;
        }
    });

    var pos = e.target.getBoundingClientRect();
    // draggableElement.style.display='none'
    dragbuffer.innerHTML='';
    dragbuffer.appendChild(draggableElement);
    // yPosition= (e.target.offsetTop - e.target.scrollTop + e.target.clientTop)-40;
    // yPosition= (pos.top)-35;
    yPosition= startY-70;
    // xPosition= (pos.left)-77;
    xPosition=startX//-79//+navPara
    draggableElement.style.transform = 'translate3d(' + xPosition + 'px, ' + yPosition + 'px, 0)';
    // 标记为正在拖动
    isDragging = true;
    touchX=e.touches[0].clientX
    touchY=e.touches[0].clientY
}

function LongTouchstartdrag(e) {
    // 阻止默认的触摸行为，比如滚动
    e.preventDefault();
    e.stopPropagation()
    console.log("touchstartdrag")
    // 记录开始拖动时的位置
    startX = e.touches[0].clientX;
    startY = e.touches[0].clientY;

    let element=e.target
    while (element!==this){
        if (element.nodeType === 1){
            element=element.parentNode
        }else {
            return
        }
    }
    draggableElement=element.cloneNode(true)
    selectdragElement=element;
    draggableElement.setAttribute('class','dragleftbuffer')
    let style=window.getComputedStyle(selectdragElement)
    styleProperties.forEach(function(styleName) {
        var styleValue = style.getPropertyValue(styleName);
        if (styleValue) {
            draggableElement.style[styleName] = styleValue;
        }
    });

    var pos = e.target.getBoundingClientRect();
    // draggableElement.style.display='none'
    dragbuffer.innerHTML='';
    dragbuffer.appendChild(draggableElement);
    // yPosition= (e.target.offsetTop - e.target.scrollTop + e.target.clientTop)-40;
    // yPosition= (pos.top)-35;
    yPosition= startY-70;
    // xPosition= (pos.left)-77;
    xPosition=startX//-79//+navPara
    draggableElement.style.transform = 'translate3d(' + xPosition + 'px, ' + yPosition + 'px, 0)';
    // 标记为正在拖动
    isDragging = true;
    touchX=e.touches[0].clientX
    touchY=e.touches[0].clientY
}

function touchmovedrag(e) {
    // 如果不是拖动状态，则不处理
    if (!isDragging) return;

    // 阻止默认的触摸行为，比如滚动
    e.preventDefault();

    // 计算拖动的距离
    var deltaX = e.touches[0].clientX - startX+xPosition;
    var deltaY = e.touches[0].clientY - startY+yPosition;
    // console.log("deltaX"+deltaX)
    // console.log("deltaY"+deltaY)
    // 在这里处理拖动逻辑，比如更新元素位置
     draggableElement.style.transform = 'translate3d(' + deltaX + 'px, ' + deltaY + 'px, 0)';
    draggableElement.style.display='flex'
    // 更新开始位置，以便计算接下来的移动距离
    touchX=e.touches[0].clientX
    touchY=e.touches[0].clientY
    // startX = e.touches[0].clientX;
    // startY = e.touches[0].clientY;
}

function touchenddrag(e) {
    // 标记为不再拖动
    var touch = e.touches[0]; // 获取第一个触摸点
     console.log("touch"+touch)
    // 获取触摸点在页面上的位置
    var element = document.elementFromPoint(touchX, touchY);
    // let showfileelement = document.getElementById('showfile')
    element=getDragUnder(element,touchX,touchY)
    // element=getUnder(element,touchX,touchY)

    var sct=isElementInsideRightUlLi(element)
    if (sct){
        let buffer=draggableElement.cloneNode(true);
        // let style=window.getComputedStyle(buffer)
        buffer.style = '';
        // styleProperties.forEach(function(styleName) {
        //     buffer.style[styleName] = '';
        //     // style.setProperty(styleName,'initial')
        //
        // });
        // buffer.style.transform = 'translate3d(' + 0 + 'px, ' + 0 + 'px, 0)';
        buffer.setAttribute('class','dropright')
        addtouchdragRight(buffer)
        sct.appendChild(buffer)
    }else {
        if (areDataSetsEqual(element,draggableElement)){
            if (viewState){
                isShowFile()
            }else {
                viewFile()
            }
            scrollToEle(document.getElementById('cloudeOpertor'))
        }else {
            // element=getUnder(element,touchX,touchX)
            if (element.classList.contains("cloudeOpertor")){
                draggableElement=element.cloneNode(true)
                if (viewState){
                    isShowFile()
                }else {
                    viewFile()
                }
            }
            if (element.classList.contains('showfile')){
                isShowFile()
            }
            // if (element.querySelector("#viewFileCon")!==null){
            //     viewFile()
            // }
            if (element.id==='viewFileCon'){
                viewFile()
            }
        }
    }
    isDragging = false;
    dragbuffer.removeChild(draggableElement)
    selectdragElement=null;
    adddrag()
    // 在这里处理拖动结束的逻辑，比如动画或位置更新

    uploadTendMap();
}

function touchendRightDel(e) {
    // 标记为不再拖动
    var touch = e.touches[0]; // 获取第一个触摸点
    console.log("touch"+touch)
    // 获取触摸点在页面上的位置
    var element = document.elementFromPoint(touchX, touchY);
    element=getDragUnder(element,touchX,touchY)
    // while (element !==this ){
    //     if (element.nodeType === 1){
    //         element=element.parentNode
    //     }else {
    //         return
    //     }
    //
    // }
    // let parent = element;
    // var i=0;
    // while (parent && parent.nodeType === 1) { // 确保parent是元素节点
    //     if ( parent.tagName==="LI") {
    //         break
    //     }
    //     i++;
    //     if (i>3){break}
    //     parent = parent.parentNode;
    // }
    // element=getUnder(element,touchX,touchY)
    // var sct=insideTochdragover(element,'tdragover')
    if (element.classList.contains('del')){
        dragbuffer.removeChild(draggableElement)
        selectdragElement.parentNode.removeChild(selectdragElement);
        let path=selectdragElement.dataset.locadpath
        fetch("http://" + address + ":" + port + "/map/ActionCloude/removeCloudeTrigger?" + path)
            .then((Response)=> {
                console.log(Response)})
    }
    selectdragElement=null;
    isDragging = false;
    dragbuffer.removeChild(draggableElement)
    adddrag()
    // 在这里处理拖动结束的逻辑，比如动画或位置更新
    uploadTendMap();
}
function touchendRightDel2(ele,touchX,touchY) {
    // 获取触摸点在页面上的位置
    let element = document.elementFromPoint(touchX, touchY);
    element=getDragUnder(element,touchX,touchY)
    if (element.classList.contains('del')){
        // dragbuffer.removeChild(draggableElement)
        let path=ele.dataset.locadpath
        fetch("http://" + address + ":" + port + "/map/ActionCloude/removeCloudeTrigger?" + path)
            .then((Response)=> {
                ele.parentNode.removeChild(ele)
                // adddrag()
                // 在这里处理拖动结束的逻辑，比如动画或位置更新
                // uploadTendMap();
                console.log(Response)}
            ).then(()=>{
            dragload()
        })
    }
    isDragging = false;

}

function touchendRight(e) {
    // 标记为不再拖动
    var touch = e.touches[0]; // 获取第一个触摸点
    console.log("touch"+touch)
    // 获取触摸点在页面上的位置
    var element = document.elementFromPoint(touchX, touchY);
    element=getDragUnder(element,touchX,touchY)
    // element=getUnder(element,touchX,touchY)
    var sct=element.classList.contains('left')
    var sct1=element.classList.contains('dragleft')
    if (sct||sct1){
        selectdragElement.parentNode.removeChild(selectdragElement);
        isDragging = false;
    }
    dragbuffer.removeChild(draggableElement)
    selectdragElement=null;
    // 在这里处理拖动结束的逻辑，比如动画或位置更新
    uploadTendMap();
}


function isElementInsideRightUlLi(element) {
    // 遍历元素的祖先元素
    var parent = element;
    while (parent && parent.nodeType === 1) { // 确保parent是元素节点
        // 检查父元素是否是ul，并且拥有.right类
        // if (parent.tagName.toLowerCase() === 'ul' && parent.classList.contains('right')) {
        //     // 检查当前元素是否是此ul的直接li子元素
        //     if ( element.tagName.toLowerCase() === 'li') {
        //         return parent;
        //     }
        // }
        if ( parent.tagName.toLowerCase() === 'li') {
            if (parent.classList.contains('right')) {
                return parent;
            }else {
                return false
            }

        }
        // 继续向上查找父元素
        parent = parent.parentNode;
    }
    // 没有找到符合条件的祖先元素
    return false;
}
function insideTochdrag(element, attr="tdrag") {
    // 遍历元素的祖先元素
    let parent = element;
    while (parent && parent.nodeType === 1) { // 确保parent是元素节点
        if ( parent.hasAttribute(attr)) {
            let value = element.getAttribute('even1');
            if (value==="yes"){
                return parent;
            }
        }
        // 继续向上查找父元素
        parent = parent.parentNode;
    }
    // 没有找到符合条件的祖先元素
    return false;
}
function insideTochdragover(element, attribute) {
    // 遍历元素的祖先元素
    let parent = element;
    while (parent && parent.nodeType === 1) { // 确保parent是元素节点
        if ( parent.hasAttribute(attribute)) {
            let value = element.getAttribute(attribute);
            if (value==="yes"){
                return parent;
            }
        }
        // 继续向上查找父元素
        parent = parent.parentNode;
    }
    // 没有找到符合条件的祖先元素
    return false;
}
function getUnder(e,x,y) {
    if (e) {
        // 临时隐藏顶层元素
        let view=e.style.visibility;
        e.style.visibility = 'hidden';
        // 获取隐藏顶层元素后露出的元素
        let belowElement = document.elementFromPoint(x, y);
        // 恢复顶层元素的可见性
        // e.style.visibility = 'visible';
        e.style.visibility = view;
        // 返回下层元素
        return belowElement;
    }
}

function isShowFile(element) {
    // 遍历元素的祖先元素
    let dragelement = draggableElement
    let target = dragelement.dataset.target;
    let absolute = dragelement.dataset.absolute;
    let abpath = absolute + target
    if (dataShowFile[abpath] !== null || dataShowFile[abpath] !== undefined) {
        dataShowFile = {}
        let obj = {}
        obj['ab'] = abpath
        obj['name'] = window.username
        // let abs=ab.split('/')
        obj['path'] = target
        let param = JSON.stringify(obj)
        fetch("http://" + address + ":" + port + "/map/show/cPathList1?" + param)
            .then((Response) => Response.json())
            .then((json) => {

                // let keys = Object.keys(json);
                let divcontains = document.createElement("div");
                divcontains.setAttribute('class', 'filecontain');

                let filelist = document.createElement("div");
                filelist.setAttribute('class', 'filelist');
                filelist.dataset.p=abpath

                let del=document.createElement("div");
                del.setAttribute('class','delfileContarin')
                del.addEventListener('click',delbutton)
                filelist.appendChild(del)
                // let div = document.createElement("div")
                let ss;
                let divn;
                for (let key in json) {
                    divn = document.createElement("div")
                    if (json[key] === 'f') {
                        divn.setAttribute('class', 'file')
                        divn.setAttribute('filename', key)
                        divn.addEventListener('click', openCloudeFile)
                    } else {
                        divn.setAttribute('class', 'path')
                        divn.setAttribute('pathname', key)
                        divn.addEventListener('click', getCloudePathListson)
                    }
                    ss = key.split('/')

                    divn.innerText = ss[ss.length - 1]
                    divn.dataset.abpath = key
                    filelist.appendChild(divn)
                }
                divcontains.dataset.ab = abpath
                divcontains.dataset.name = window.username


                divcontains.appendChild(filelist)
                let showfiles = document.getElementsByClassName('showfile');
                showfiles.item(0).appendChild(divcontains)
                filelist.dataset.sn = '0';
                dataShowFile[0] = target;
                showOperaFeild()
                scrollToEle(opertaContainer,4)

                addDragTouchParent(filelist,touchEvenFun,touchMoveEvenFun,touchLongFun,['path','file'],
                    document.getElementById('dragbuffer'),
                    "",800)
                addDragMouseParent(filelist,mouseEvenFun,mouseMoveEvenFun,mouseLongFun,['path','file'],
                    document.getElementById('dragbuffer'),
                    "",800)
            })
    }
    // 没有找到符合条件的祖先元素
    return false;
}

function showOperaFeild() {
    opertaContainer.style.width='100vw'
}

function delbutton(e) {
    this.parentNode.parentNode.parentNode.removeChild(this.parentNode.parentNode)
    let showFiles=document.querySelector('#showfile')
    if (viewState){
        if (showFiles.childElementCount<1){
            opertaContainer.style.display='388px'
        }
    }else {
        if (showFiles.childElementCount<1){
            opertaContainer.style.display='388px'
        }
    }
}

let changeBut=$('#cheakButton').click('click',cheakView)


let viewState=true
function cheakView() {
    if (viewState){
        document.querySelector(".showfile").style.display='none'
        document.querySelector("#viewFileCon").style.display='flex'
    }else {
        document.querySelector(".showfile").style.display='flex'
        document.querySelector("#viewFileCon").style.display='none'
    }
    viewState=!viewState
}

loaduser()

function touchLongFun(x, y) {
    let ele = getDragsUnder(x, y)
    if (ele.closest('.file') || ele.closest('.path') ) {
        console.log('方法 1: 是 .file 或其子元素');
    }else {
        return
    }
    let co = document.createElement('div')
    co.setAttribute('class', 'CFPopera')
    let del = document.createElement('div')
    del.setAttribute('class', 'CFPopDel')
    ele.appendChild(co)
    co.appendChild(del)

    del.textContent = "删除"
    del.addEventListener('click', function () {
        console.log('del')
    })
    co.addEventListener('click', function (e) {
        e.stopPropagation()
        e.preventDefault()
        if (e.target === this) {
            e.target.parentNode.removeChild(this)
        }
        console.log('clear')
    })
}


function touchEvenFun(even,dragEle){
    let at=even.changedTouches
    let x=at[at.length-1].clientX
    let y=at[at.length-1].clientY
    let ele=getDragsUnder(x,y)

        // ||ele.classList.contains('file')
    try {
        if (ele.dataset.abpath===dragEle.dataset.abpath){
            let at=even.changedTouches
            let x=at[at.length-1].clientX
            let y=at[at.length-1].clientY
            let ele=getDragsUnder(x,y)
            let co=document.createElement('div')
            co.setAttribute('class','CFPopera')
            let del=document.createElement('div')
            del.setAttribute('class','CFPopDel')
            del.textContent='删除'
            ele.appendChild(co)
            co.appendChild(del)
            let data={}
            data['file']=ele.dataset.abpath
            data['user']=ele.parentNode.parentNode.dataset.name
            data['path']=ele.parentNode.parentNode.dataset.ab
            del.addEventListener('click',function (e) {
                e.stopPropagation()
                e.preventDefault()
                fetch("http://" + address + ":" + port + "/map/ActionCloude/removeCloudeFile"
                    , {
                        method: 'post',
                        body: JSON.stringify(data),
                        headers: {'Content-Type': 'application/json',},}
                ).then(Response=>{
                    if(Response.ok){
                        ele.parentNode.removeChild(ele)
                    }
                })
                console.log('del')
            })
            co.addEventListener('click',function (e) {
                e.stopPropagation()
                e.preventDefault()
                if(e.target===this){
                    e.target.parentNode.removeChild(this)
                }
                console.log('clear')
            })
        }
    }catch (e){
        console.log(e)
    }



    if (ele.classList.contains('path')){
        let f0=ele.parentNode.parentNode.dataset.ab
        let na=ele.parentNode.parentNode.dataset.name
        let ab=even.target.dataset.abpath

        let data = {}
        data['name'] = na;
        data['file'] = ab
        data['path'] = ele.dataset.abpath
        let d=JSON.stringify(data)
        console.log(JSON.stringify(data))
        fetch("http://" + address + ":" + port + "/map/ActionCloude/moveCloudeFile?" + d
            , {
                method: 'post',
                body: d,
                headers: {'Content-Type': 'application/json',},}
        )
    }
    if (ele.classList.contains('delfileContarin')){
        let f0=ele.parentNode.parentNode.dataset.ab
        let na=ele.parentNode.parentNode.dataset.name
        let ab=even.target.dataset.abpath

        let data = {}
        data['name'] = na;
        data['file'] = ab
        data['path'] = f0
        let d=JSON.stringify(data)
        console.log(d)
        fetch("http://" + address + ":" + port + "/map/ActionCloude/moveCloudeFile?" + d
            , {
                method: 'post',
                body: d,
                headers: {'Content-Type': 'application/json',},}
        )
    }
    if (ele.classList.contains('filelist')){
        let f0=ele.dataset.p
        let na=ele.parentNode.dataset.name
        let ab=even.target.dataset.abpath

        let data = {}
        data['name'] = na;
        data['file'] = ab
        data['path'] = f0
        let d = JSON.stringify(data)
        console.log(JSON.stringify(data))
        fetch("http://" + address + ":" + port + "/map/ActionCloude/moveCloudeFile?" + d
            , {
                method: 'post',
                body: d,
                headers: {'Content-Type': 'application/json',},}
        )
    }
    if (ele.classList.contains('uplay')){
        let p=ele.parentNode.dataset.p
        let na=ele.parentNode.parentNode.dataset.name
        let ab=even.target.dataset.abpath

        let data = {}
        data['name'] = na;
        data['file'] = ab
        data['path'] = p
        let d = JSON.stringify(data)
        console.log(JSON.stringify(data))
        fetch("http://" + address + ":" + port + "/map/ActionCloude/moveCloudeFile?" + d
            , {
                method: 'post',
                body: d,
                headers: {'Content-Type': 'application/json',},}
        )
    }
    console.log('longtime')
    console.log('longtime')
}

function touchMoveEvenFun(even){

    let x=even.touches[0].clientX
    let y=even.touches[0].clientY
    let ele=getDragsUnder(x,y)

    if (ele.classList.contains('path')){
        getCloudePathListson.bind(ele)()
        return true
    }
    if (ele.classList.contains('menuToggle')){
        if (leftbodystate) {
            leftbody.style.display = 'none'
            rightCon.style.marginLeft = '0px'
            leftbodystate = false;
            navPara=78;
        } else {
            leftbody.style.display = ''
            rightCon.style.marginLeft = '78px'
            leftbodystate = true;
            navPara=0;
        }
        // menuToggle.style.display='flex'
        menuToggle.classList.toggle('active')
        return true
    }
    if (ele.classList.contains('icon')){
        window.username=ele.dataset.username
        lodahtml(true)
        return true
    }
    if (ele.classList.contains('dragleft')){
        draggableElement=ele.cloneNode(true)
        if (viewState){
            isShowFile()
        }else {
            viewFile()
        }
        return true
    }

    console.log('longtime')
}

function mouseLongFun(x,y){
    let ele=getDragsUnder(x,y)
    let co=document.createElement('div')
    co.setAttribute('class','CFPopera')
    let del=document.createElement('div')
    del.setAttribute('class','CFPopDel')
    ele.appendChild(co)
    co.appendChild(del)

    del.textContent="删除"
    del.addEventListener('click',function () {
        console.log('del')
    })
    co.addEventListener('click',function (e) {
        e.stopPropagation()
        e.preventDefault()
        if(e.target===this){
            e.target.parentNode.removeChild(this)
        }
        console.log('clear')
    })
}
function mouseEvenFun(even,dragEle){
    let x=even.clientX
    let y=even.clientY
    let ele=getDragsUnder(x,y)

    // ||ele.classList.contains('file')
    try {
        if (ele.dataset.abpath===dragEle.dataset.abpath){
            let co=document.createElement('div')
            co.setAttribute('class','CFPopera')
            let del=document.createElement('div')
            del.setAttribute('class','CFPopDel')
            del.textContent='删除'
            ele.appendChild(co)
            co.appendChild(del)
            let data={}
            data['file']=ele.dataset.abpath
            data['user']=ele.parentNode.parentNode.dataset.name
            data['path']=ele.parentNode.parentNode.dataset.ab
            del.addEventListener('click',function (e) {
                e.stopPropagation()
                e.preventDefault()
                fetch("http://" + address + ":" + port + "/map/ActionCloude/removeCloudeFile"
                    , {
                        method: 'post',
                        body: JSON.stringify(data),
                        headers: {'Content-Type': 'application/json',},}
                ).then(Response=>{
                    if(Response.ok){
                        ele.parentNode.removeChild(ele)
                    }
                })
                console.log('del')
            })
            co.addEventListener('click',function (e) {
                e.stopPropagation()
                e.preventDefault()
                if(e.target===this){
                    e.target.parentNode.removeChild(this)
                }
                console.log('clear')
            })
        }
    }catch (e){
        console.log(e)
    }



    if (ele.classList.contains('path')){
        let f0=ele.parentNode.parentNode.dataset.ab
        let na=ele.parentNode.parentNode.dataset.name
        let ab=even.target.dataset.abpath

        let data = {}
        data['name'] = na;
        data['file'] = ab
        data['path'] = ele.dataset.abpath
        let d=JSON.stringify(data)
        console.log(JSON.stringify(data))
        fetch("http://" + address + ":" + port + "/map/ActionCloude/moveCloudeFile?" + d
            , {
                method: 'post',
                body: d,
                headers: {'Content-Type': 'application/json',},}
        )
    }
    if (ele.classList.contains('delfileContarin')){
        let f0=ele.parentNode.parentNode.dataset.ab
        let na=ele.parentNode.parentNode.dataset.name
        let ab=even.target.dataset.abpath

        let data = {}
        data['name'] = na;
        data['file'] = ab
        data['path'] = f0
        let d=JSON.stringify(data)
        console.log(d)
        fetch("http://" + address + ":" + port + "/map/ActionCloude/moveCloudeFile?" + d
            , {
                method: 'post',
                body: d,
                headers: {'Content-Type': 'application/json',},}
        )
    }
    if (ele.classList.contains('filelist')){
        let f0=ele.dataset.p
        let na=ele.parentNode.dataset.name
        let ab=even.target.dataset.abpath

        let data = {}
        data['name'] = na;
        data['file'] = ab
        data['path'] = f0
        let d = JSON.stringify(data)
        console.log(JSON.stringify(data))
        fetch("http://" + address + ":" + port + "/map/ActionCloude/moveCloudeFile?" + d
            , {
                method: 'post',
                body: d,
                headers: {'Content-Type': 'application/json',},}
        )
    }
    if (ele.classList.contains('uplay')){
        let p=ele.parentNode.dataset.p
        let na=ele.parentNode.parentNode.dataset.name
        let ab=even.target.dataset.abpath

        let data = {}
        data['name'] = na;
        data['file'] = ab
        data['path'] = p
        let d = JSON.stringify(data)
        console.log(JSON.stringify(data))
        fetch("http://" + address + ":" + port + "/map/ActionCloude/moveCloudeFile?" + d
            , {
                method: 'post',
                body: d,
                headers: {'Content-Type': 'application/json',},}
        )
    }
    console.log('longtime')
    console.log('longtime')
}

function mouseMoveEvenFun(even){

    let x=even.clientX
    let y=even.clientY
    let ele=getDragsUnder(x,y)

    if (ele.classList.contains('path')){
        getCloudePathListson.bind(ele)()
        return true
    }
    if (ele.classList.contains('menuToggle')){
        if (leftbodystate) {
            leftbody.style.display = 'none'
            rightCon.style.marginLeft = '0px'
            leftbodystate = false;
            navPara=78;
        } else {
            leftbody.style.display = ''
            rightCon.style.marginLeft = '77px'
            leftbodystate = true;
            navPara=0;
        }
        // menuToggle.style.display='flex'
        menuToggle.classList.toggle('active')
        return true
    }
    if (ele.classList.contains('icon')){
        window.username=ele.dataset.username
        lodahtml(true)
        return true
    }
    if (ele.classList.contains('dragleft')){
        draggableElement=ele.cloneNode(true)
        if (viewState){
            isShowFile()
        }else {
            viewFile()
        }
        return true
    }

    console.log('longtime')
}





// function setCloudTouchRS(eleImg) {
//     let touchMoved=false;
//     let currentDistance = 0
//     // const getDistance = (start, stop) => Math.hypot(stop.x - start.x, stop.y - start.y)
//     let posStart= {
//         x: 0, y: 0
//     };
//     let tranStart=[]
//     let lastDistance = 0
//
//     function getDistance(p1, p2) {
//         const dx = p2.x - p1.x;
//         const dy = p2.y - p1.y;
//         return Math.sqrt(dx * dx + dy * dy);
//     }
//
// // 计算两点中点
//     function getMidpoint(p1, p2) {
//         return {
//             x: (p1.x + p2.x) / 2,
//             y: (p1.y + p2.y) / 2
//         };
//     }
//     let moveFun=function (event) {
//         event.stopPropagation()
//         event.preventDefault()
//         const touches = event.touches
//         const events = touches[0]
//         const events2 = touches[1]
//         if (events2) {
//             resize(eleImg,
//                 {x: events.pageX, y: events.pageY},
//                 {x: events2.pageX, y: events2.pageY})
//             touchMoved=true
//         }else {
//             let dx=events.pageX-posStart.x
//             let dy=events.pageY-posStart.y
//             if(Math.abs(dx)+Math.abs(dy)>4){
//                 touchMoved=true
//             }
//             toPos(eleImg,events)
//             let maxEle=getMaxEle()
//             let ps={
//                 left:true ,
//                 top:true ,
//                 right:true,
//                 bottom:true
//             }
//             let mfun
//             if (maxEle.id==='cloudeOpertor'){
//                 // ps=isElementOutOfBounds(document.getElementById('showPib'),eleImg)
//                 // mfun=openCloudeFile.bind(selectFileEle)
//                 ps=isElementOutOfBounds(document.querySelector('#showFile .view'),eleImg)
//             }
//             // if (maxEle.id==='content-b'){
//             //     ps=isElementOutOfBounds(document.querySelector('#showFile .view'),eleImg)
//             //
//             // }
//
//             if ((!ps.left)||(!ps.top) ){
//                 while (selectFileEle.previousElementSibling!=null){
//
//                     let f=selectFileEle.previousElementSibling.querySelector('.path')
//                     if(f!=null){
//                         let ne = true
//                         let bf= selectFileEle
//                         while (ne && f!=null){
//                             selectFileEle=selectFileEle.previousElementSibling
//                             f=selectFileEle.querySelector('.path')
//                         }
//                         if (selectFileEle.previousElementSibling.getAttribute('filename')!==null||
//                             selectFileEle.previousElementSibling.querySelector('.file') !== null){
//                             selectFileEle=selectFileEle.previousElementSibling
//                         }else {
//                             selectFileEle = bf
//                             break
//                         }
//                     }
//                     if (selectFileEle.previousElementSibling.getAttribute('filename')!==null||
//                         selectFileEle.previousElementSibling.querySelector('.file') !== null
//                     ){
//                         selectFileEle=selectFileEle.previousElementSibling
//                         break
//                     }else {
//                         break
//                     }
//                     // selectFileEle=selectFileEle.previousElementSibling
//                 }
//                 mfun=openCloudeFile.bind(selectFileEle)
//                 mfun()
//                 eleImg.removeEventListener('touchmove',moveFun)
//                 return false
//             }else {
//                 if ((!ps.right)||(!ps.bottom) ){
//                     while (selectFileEle.nextElementSibling!=null){
//                         let f=selectFileEle.nextElementSibling.querySelector('.path')
//                         if(f!=null){
//                             let ne = true
//                             let bf= selectFileEle
//                             while (ne && f!=null){
//                                 selectFileEle=selectFileEle.nextElementSibling
//                                 f=selectFileEle.querySelector('.path')
//                             }
//                             if (selectFileEle.nextElementSibling.getAttribute('filename')!==null||
//                                 selectFileEle.nextElementSibling.querySelector('.file') !== null){
//                                 selectFileEle=selectFileEle.nextElementSibling
//                             }else {
//                                 selectFileEle = bf
//                                 break
//                             }
//                         }
//                         if (selectFileEle.nextElementSibling.getAttribute('filename')!==null||
//                             selectFileEle.nextElementSibling.querySelector('.file') !== null){
//                             selectFileEle=selectFileEle.nextElementSibling
//                             break
//                         }else {
//                             break
//                         }
//                         // selectFileEle=selectFileEle.nextElementSibling
//                     }
//
//                     mfun(selectFileEle)
//                     eleImg.removeEventListener('touchmove',moveFun)
//                     return false
//                 }
//             }
//         }
//     }
//     eleImg.addEventListener('touchmove',moveFun , {passive: false})
//     eleImg.addEventListener('touchstart', function (event) {
//             event.stopPropagation()
//             event.preventDefault()
//             const touches = event.touches
//             const events = touches[0]
//             const events2 = touches[1]
//             if (events2) {
//                 const p1 = { x: touches[0].pageX, y: touches[0].pageY };
//                 const p2 = { x: touches[1].pageX, y: touches[1].pageY };
//                 lastDistance = getDistance(p1, p2);
//             }else {
//                 posStart.x=events.pageX;
//                 posStart.y=events.pageY;
//                 let tr=eleImg.style.translate
//                 let numbers
//                 if (tr!==undefined){
//                     numbers = tr.match(/[+-]?\d+(\.\d+)?/g);
//                 }
//                 if (numbers===null){
//                     numbers=[]
//                     numbers[0]=0.0
//                     numbers[1]=0.0
//                 }else {
//                     numbers[0]=parseFloat(numbers[0])
//                     if (isNaN(numbers[0])||numbers[0]===null){
//                         numbers[0]=0.0
//                     }
//                     numbers[1]=parseFloat(numbers[1])
//                     if (isNaN(numbers[1])||numbers[1]===null){
//                         numbers[1]=0.0
//                     }
//                 }
//                 posStart.x += numbers[0]
//                 posStart.y += numbers[1]
//                 tranStart=numbers;
//             }
//         }
//         // , {passive: false}
//     )
//
//     let touchM = function (event) {
//         event.preventDefault()
//         const touches = event.touches
//         const events = touches[0]
//         const events2 = touches[1]
//         if (events2) resize(eleImg,
//             {x: events.pageX, y: events.pageY},
//             {x: events2.pageX, y: events2.pageY})
//     }
//     let cl = function () {
//         document.removeEventListener('touchmove', setCloudTouchRS.prototype.tm)
//     }
//
//     document.addEventListener('touchmove', touchM, {passive: false})
//     setCloudTouchRS.prototype.tm = touchM
//
//     setCloudTouchRS.prototype.cl = cl
//
//     // 【重要】需要在外部维护这个变量，用于记录上一帧的双指距离
// // 建议在 touchstart 时初始化为 0 或当前距离
//
//     function resize(dom, start, stop) {
//         // 1. 计算当前双指距离和中点（视口坐标）
//         const currentDistance = getDistance(start, stop);
//         const midpoint = getMidpoint(start, stop);
//
//         // 如果距离太小或没有变化，忽略（防止除以零或抖动）
//         if (currentDistance < 10 || Math.abs(currentDistance - lastDistance) < 1) {
//             lastDistance = currentDistance;
//             return;
//         }
//
//         // 2. 获取当前变换状态
//         const style = window.getComputedStyle(dom);
//         // 强制 origin 为 0 0，保证矩阵逻辑与 tranBig 一致
//         if (dom.style.transformOrigin !== '0px 0px') {
//             dom.style.transformOrigin = '0 0';
//         }
//
//         const matrix = new DOMMatrix(style.transform);
//         const currentScale = matrix.m11;
//         const currentTx = matrix.m41;
//         const currentTy = matrix.m42;
//
//         // 3. 计算新的缩放比例
//         // 逻辑：新缩放 = 旧缩放 * (当前距离 / 上次距离)
//         // 这样缩放是线性的、平滑的，符合手指拉伸的物理直觉
//         const scaleRatio = currentDistance / lastDistance;
//         let newScale = currentScale * scaleRatio;
//
//         // 限制缩放范围 (可选，根据需求调整)
//         newScale = Math.max(0.1, Math.min(10, newScale));
//
//         // 4. 计算缩放中心点在“原始内容坐标系”中的位置 (originX, originY)
//         // 这一步逻辑与 tranBig 完全一致，确保行为统一
//
//         // 获取当前元素的视口边界
//         const rect = dom.getBoundingClientRect();
//
//         // 计算中点相对于元素“变换后”左上角的偏移
//         const xInBox = midpoint.x - rect.left;
//         const yInBox = midpoint.y - rect.top;
//
//         // 反推原始坐标：Origin = Offset / Scale
//         const originX = xInBox / currentScale;
//         const originY = yInBox / currentScale;
//
//         // 5. 计算新的平移量 (补偿缩放)
//         // 公式推导：为了保持 midpoint 在视口中的位置不变
//         // newTx = currentTx + originX * (currentScale - newScale)
//         const newTx = currentTx + originX * (currentScale - newScale);
//         const newTy = currentTy + originY * (currentScale - newScale);
//
//         // 6. 应用变换
//         dom.style.transform = `translate(${newTx}px, ${newTy}px) scale(${newScale})`;
//
//         // 7. 更新 lastDistance 供下一帧使用
//         lastDistance = currentDistance;
//     }
//
//     function toPos(dom, event) {
//         let dx=event.pageX-posStart.x
//         let dy=event.pageY-posStart.y
//         // dx = event.x
//         let str=dom.style.transform
//
//         // const translateRegex = /translate$([^)]+)$/;
//         const translateRegex=/translate\(([^)]+)\)/
//
//         const scaleRegex = /scale$[^)]+$/;
//         let translateMatch = str.match(translateRegex);
//         if (translateMatch) {
//             let numbers = translateMatch[1].match(/-?\d*\.?\d+/g).map(function(num) {
//                 let numberss=num
//                 // 返回新的translate部分
//                 if (numberss===null){
//                     numberss=1;
//                 }else {
//                     numberss=parseFloat(numberss)
//                 }
//                 return numberss;
//             });
//             numbers[0]+=dx
//             numbers[1]+=dy
//             let newTranslatePart = "translate(" + numbers[0].toFixed(2) + "px, " + numbers[1].toFixed(2) + "px)";
//             dom.style.transform = str.replace(translateRegex, newTranslatePart);
//             posStart.x = event.pageX
//             posStart.y = event.pageY
//         } else {
//             // 如果没有找到translate，则添加translate(0px, 0px)
//             let newTranslatePart = "translate(0.00px, 0.00px)";
//             if (scaleRegex.test(str)) {
//                 // 如果存在scale，将translate插入到scale之前
//                 str = str.replace(scaleRegex, newTranslatePart + " " + function() { return arguments[0]; });
//             } else {
//                 // 如果不存在其他变换，直接在末尾添加translate
//                 str += " " + newTranslatePart;
//             }
//             dom.style.transform = str
//
//         }
//     }
// }
//
// function cancelCloudTouchRS() {
//     try {
//         setCloudTouchRS.prototype.cl();
//     }catch (e) {
//         console.log(e)
//     }
// }
