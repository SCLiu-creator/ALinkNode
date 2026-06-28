
function addtouchdrogCl(e) {
    e.addEventListener('touchstart', LtDTouchstartdrag);
    e.addEventListener('touchmove', touchmovedrag);
    e.addEventListener('touchend', LtDtouchend);
}

let swapShow=document.querySelector('#swapShow')
let dragswapCon=document.querySelector('#dragswapCon')
let swapShowstate=false
swapShow.addEventListener('click',swapShowcontroll)
function swapShowcontroll() {
    if (swapShowstate){
        dragswapCon.style.width='initial'
        swapShow.style.height='10px'
    }else {
        dragswapCon.style.width='12px'
        swapShow.style.height='102vh'
    }
    swapShowstate = !swapShowstate
}


// let selectdragElement;
let selectCldDraguser;
let selectCldDragBuffer;
function LtDTouchstartdrag(e) {
    // 阻止默认的触摸行为，比如滚动
    // e.preventDefault();
    console.log("cltouchstartdrag")
    // 记录开始拖动时的位置
    startX = e.touches[0].clientX-20;
    startY = e.touches[0].clientY;
    // draggableElement=e.target.cloneNode(true)
    selectdragElement=e.target;
    selectCldDraguser=selectdragElement.getAttribute('username')
    selectCldDraguser=selectdragElement.dataset.username
    draggableElement=document.createElement('div')
    draggableElement.setAttribute('class','CldDragBuffer')
    // 获取计算样式// 获取 '--bg' 伪类的值
    var computedStyle = window.getComputedStyle(e.target);
    draggableElement.style.backgroundColor=computedStyle.getPropertyValue('--bg')
    draggableElement.style.display='none'
    let draggableText=document.createElement('div')
    draggableText.setAttribute('class','CldDragBufferText')
    draggableText.innerText=selectCldDraguser
    draggableElement.appendChild(draggableText)
    dragbuffer.innerHTML='';
    dragbuffer.appendChild(draggableElement);
    yPosition= (e.target.offsetTop + e.target.clientTop)+startY-40;
    // yPosition=0
    draggableElement.style.transform = 'translate3d(' + 0 + 'px, ' + yPosition + 'px, 0)';
    // 标记为正在拖动
    isDragging = true;
    touchX=e.touches[0].clientX
    touchY=e.touches[0].clientY
}


let otherBin={}

function LtDtouchend(e) {
    // 标记为不再拖动
    var touch = e.touches[0]; // 获取第一个触摸点
    console.log("touch"+touch)
    // 获取触摸点在页面上的位置
    var element = document.elementFromPoint(touchX, touchY);
    var sct=insideIsDrag(element)
    if (sct){
        // dragbuffer.removeChild(draggableElement)
        // selectdragElement.parentNode.removeChild(selectdragElement);
        let bin = otherBin[selectCldDraguser]
        let dragswap=document.querySelector('#dragswap')
        if (bin!==null && bin!==undefined && bin.nodeType === 1){
        }else {
            bin= document.createElement('ul')
            dragswap.appendChild(bin)
            dragswap.style.display='flex'
            otherBin[selectCldDraguser]=bin
        }
        drageableds = document.querySelectorAll(".left ul div");
        fetch("http://" + address + ":" + port + "/map/ActionCloude/getCloudePage?" + selectCldDraguser, {withCredentials: true})
            .then((Response) => Response.json())
            .then((jsondatas) => {
                bin.innerHTML = '';
                for (var jsondata in jsondatas) {
                    var div = createLeftdiv(jsondatas[jsondata]['target'], jsondatas[jsondata]['root']);
                    // div.innerText =
                    // div.dataset.target = jsondatas[jsondata]['target']
                    // div.dataset.absolute =
                    // div.setAttribute("draggable", "true");
                    // div.setAttribute("ondrag", "handleDragStart");
                    // div.setAttribute("ondragover", "handleDragover");
                    // div.setAttribute("ondrop", "DropOut");
                    bin.appendChild(div);
                }
            });
    }
    dragbuffer.removeChild(draggableElement)
    selectdragElement=null;
    isDragging = false;
    // adddrag()
    // 在这里处理拖动结束的逻辑，比如动画或位置更新
    // uploadTendMap();
}

function insideIsDrag(element) {
    // 遍历元素的祖先元素
    let parent = element;
    while (parent && parent.nodeType === 1) { // 确保parent是元素节点
        if ( parent.classList.contains('dragContainer')) {
                return parent;
            }
        // 继续向上查找父元素
        parent = parent.parentNode;
    }
    // 没有找到符合条件的祖先元素
    return false;
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
            return parent;
        }
        // 继续向上查找父元素
        parent = parent.parentNode;
    }
    // 没有找到符合条件的祖先元素
    return false;
}
function insideTochdrag(element) {
    // 遍历元素的祖先元素
    let parent = element;
    while (parent && parent.nodeType === 1) { // 确保parent是元素节点
        if ( parent.hasAttribute('tdrag')) {
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
function insideTochdragover(element) {
    // 遍历元素的祖先元素
    let parent = element;
    while (parent && parent.nodeType === 1) { // 确保parent是元素节点
        if ( parent.hasAttribute('tdragover')) {
            let value = element.getAttribute('tdragover');
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

function isShowFilecl(element) {
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
                showfiles = document.getElementsByClassName('showfile');
                showfiles.item(0).appendChild(divcontains)
                filelist.dataset.sn = 0;
                dataShowFile[0] = target;
            })
    }
    // 没有找到符合条件的祖先元素
    return false;
}

function delbutton(e) {
    this.parentNode.parentNode.parentNode.removeChild(this.parentNode.parentNode)
    opertaContainer.style=null

    scrollToEle(document.querySelector(".dragContainer"))
}