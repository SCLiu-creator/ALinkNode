console.log("draginside")

let insideleft=document.querySelector('#leftcl')
insideleft.addEventListener('click',insideleftchange)
let insideleftstate=true
function insideleftchange(e) {
    let node=document.querySelector('.left')
    if (insideleftstate){
        node.style.width = '20px'
        e.target.style.height= '100vh';
        e.target.style.borderRadius= '0 13px  13px 0'; /* 左边圆角 */
        insideleftstate=false;
    }else {
        node.style.width = '140px'
        e.target.style.height= '22px';
        e.target.style.borderRadius= '0 0 0 20px'; /* 左边圆角 */
        insideleftstate=true;
    }
}



let insideright=document.querySelector('#rightcl')
insideright.addEventListener('click',insiderightchange)
let insiderightstate=true
function insiderightchange(e) {

    let node=document.querySelector('.right')
    let dl=document.querySelector('#dl')
    if (insiderightstate){
        node.style.width = '20px'
        node.style.minWidth='20px';
        dl.style.width = 'auto'
        e.target.style.height= '100vh';
        e.target.style.borderRadius= '13px 0 0  13px'; /* 左边圆角 */
        insiderightstate=false;
    }else {
        // dl.style.width = '470px'
        dl.style.width = null
        // node.style.width = '318px'
        node.style.minWidth=null;
        node.style.width = null
        e.target.style.height= '22px';
        e.target.style.borderRadius= '0 0  20px 0'; /* 左边圆角 */
        insiderightstate=true;
    }
}


let side = document.getElementsByClassName("dragSide")[0]
side.addEventListener('mousedown', startSide);
side.addEventListener('mousemove', dragSide);
side.addEventListener('mouseup', overSide);
side.addEventListener('mouseleave', overSide);
let sidepost;
let sidemousestate;
function startSide(e) {
    console.log(e.target);
    sidepost = e.clientX;
    sidemousestate = true
}

function dragSide(e) {
    if (sidemousestate) {
        // sidepost=e.clientX;
        lo = e.clientX;
        dx = lo - sidepost;
        containwidth = dragContainer.offsetWidth;
        // containwidth = opertaContainer.offsetWidth;
        oAswidth = opertaAreasid.offsetWidth
        // dragContainerside=containwidth.split("px")[0]
        dx=dx/4
        nsd = (containwidth + dx)
        osd = (oAswidth + dx)
        // dragContainer.offsetWidth=nsd + 'px'
        // dragContainer.style.width=nsd +10+ 'px'
        opertaContainer.style.width = osd+20+ 'px'
        opertaAreasid.style.width = osd + 'px'
        showAreasid.style.width = osd + 'px'
        // side.style.transform = osd + 'px'
    }
    // window.getComputedStyle(side).getPropertyValue('width')
}

function overSide(e) {
    console.log(e.target);
    sidemousestate = false
}