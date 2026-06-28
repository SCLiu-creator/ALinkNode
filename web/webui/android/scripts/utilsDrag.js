// 创建一个自定义事件
const customEvent = new CustomEvent('customClear', {
    detail: {
        message: 'This is a custom event'
    },
    bubbles: true,
    cancelable: true
});
const customEventT = new CustomEvent('customClearT', {
    detail: {
        message: 'This is a custom event'
    },
    bubbles: true,
    cancelable: true
});

function addLongTouch(ele,fun,time) {
    ele.dispatchEvent(customEventT)
    let state=false
    let id;
    function start() {
        state=false
        id=setTimeout(()=>{
            state=true;
        },time)
    }
    function cancel() {
        clearTimeout(id)
        state=false
    }
    function end() {
        if (state){
            fun()
        }
    }
    ele.addEventListener('touchstart',start)
    ele.addEventListener('touchmove',cancel)
    ele.addEventListener('touchcancel',cancel)
    ele.addEventListener('touchend',end)
    ele.addEventListener('customClearT',function (){
        ele.removeEventListener('touchstart',start)
        ele.removeEventListener('touchmove',cancel)
        ele.removeEventListener('touchcancel',cancel)
        ele.removeEventListener('touchend',end)
    })
}

function addLongTouchParent(ele,fun,matches,time,bats=10) {
    ele.dispatchEvent(customEventT)
    let state=false
    let id;
    let target
    let x
    let y
    function start(e) {
        target=e.target
        // 获取当前触摸点的坐标
        const touch = e.touches[0];
        x = touch.clientX;
        y = touch.clientY;

        state=false
        id=setTimeout(()=>{
            state=true;
        },time)
    }
    function cancel(e) {
        target=e.target
        // 获取当前触摸点的坐标
        let touch = e.touches[0];
        if (touch == null){
            touch=e.changedTouches[0]
        }
        if(Math.abs(touch.clientX-x)<bats && Math.abs(touch.clientY-y)<bats){
            return
        }
        clearTimeout(id)
        state=false
    }
    function end(event) {
        if (state){
            if (matches.some(className => event.target.classList.contains(className))){
                fun(event)
            }
            // if (event.target.matches(matches)) {
            //
            // }
        }
    }
    ele.addEventListener('touchstart',start)
    ele.addEventListener('touchmove',cancel)
    ele.addEventListener('touchcancel',cancel)
    ele.addEventListener('touchend',end)
    ele.addEventListener('customClearT',function (){
        ele.removeEventListener('touchstart',start)
        ele.removeEventListener('touchmove',cancel)
        ele.removeEventListener('touchcancel',cancel)
        ele.removeEventListener('touchend',end)
    })
}

function addLongMouseParent(ele,fun,matches,time,bats=10) {
    ele.dispatchEvent(customEvent)
    let state=false
    let id;
    let target
    let x
    let y
    function start(e) {
        target=e.target
        // 获取当前触摸点的坐标
        x = e.clientX;
        y = e.clientY;

        state=false
        id=setTimeout(()=>{
            state=true;
        },time)
    }
    function cancel(e) {
        // if( ! e){  // 兼容IE浏览器
        //     e = window.event;
        //     e.target = e.srcElement;
        //     e.layerX = e.offsetX;
        //     e.layerY = e.offsetY;
        // }
        target=e.target
        // 获取当前触摸点的坐标
        if(Math.abs(e.clientX-x)<bats && Math.abs(e.clientY-y)<bats){
            return
        }
        clearTimeout(id)
        state=false
    }
    function end(event) {
        if (state){
            if (event.target.matches(matches)) {
                fun(event)
            }
        }
    }
    ele.addEventListener('mousedown',start)
    ele.addEventListener('mousemove',cancel)
    ele.addEventListener('mouseout',cancel)
    ele.addEventListener('mouseup',end)
    ele.addEventListener('customClear',function (){
        ele.removeEventListener('mousedown',start)
        ele.removeEventListener('mousemove',cancel)
        ele.removeEventListener('mouseout',cancel)
        ele.removeEventListener('mouseup',end)
    })
}
document.addEventListener('contextmenu', (e) => {
    e.preventDefault(); // 阻止默认右键菜单
});
function addDragTouchParent(ele,fun,funMov,funLong,startCla=[""],
                            dragbuffer=document.getElementById('dragbuffer'),
                            matches,time) {
    ele.dispatchEvent(customEventT)
    let isDragging=false
    let id;
    let target
    let xPosition
    let yPosition
    let dragEle
    let touchEle
    let touchX,touchY
    let startX,startY
    let startTime
    let longTouch
    let touchesStart
    let touchStartEle
    function start(e) {
        // 标记为正在拖动
        id=setTimeout(()=>{
            // isDragging=false
            // let at=e.touches[0]
            // let x=at.clientX
            // let y=at.clientY
            longTouch=true
            ele.style.overflow='hidden'
            // funLong(x,y)
        },time)
        touchStartEle=e.target
        touchesStart=e.touches
        startTime=Date.now()
        // 标记为正在拖动
        isDragging = true;
        longTouch=false
        // e.target.parentNode.style.setProperty('touch-action','none')
        // e.target.parentNode.style.setProperty('overflow','hide')
        return false
        // 获取当前触摸点的坐标
    }

    function move(e) {
        clearTimeout(id)
        if (!isDragging || !longTouch) {return}
        else {
            if (dragEle==null){
                if ((Date.now()-startTime)<300){
                    return
                }
                const touch = e.touches[0]; // 获取第一个触摸点
                const targetElement = document.elementFromPoint(touch.clientX, touch.clientY);

                if (touchStartEle.contains(targetElement)) {
                    console.log('触摸未离开目标元素');
                    return
                    // 可以在这里触发自定义逻辑（如取消选中状态）
                }
                e.preventDefault()
                touchX=e.touches[0].clientX
                touchY=e.touches[0].clientY
                target=e.target
                console.log("touchstartdrag")
                // 记录开始拖动时的位置
                startX = e.touches[0].clientX;
                startY = e.touches[0].clientY;
                let element=e.target
                element.style.setProperty('--my-tm','sy')
                // element.parentNode.style.setProperty('touch-action','none')

                // while (!element.classList.contains(startCla)){
                let b=startCla.some(className => element.classList.contains(className))
                while (element){
                    if (startCla.some(className => element.classList.contains(className))){
                        break
                    }else {
                        if (!ele.contains(element)){
                            return;
                        }else {
                            element=element.parentNode
                        }
                    }
                }
                touchEle=element
                dragEle=element.cloneNode(true)
                let vc=document.getElementById('viewFileCon');
                dragEle.dataset.name=vc.dataset.name

                // dragEle.style=element.style
                // dragEle.style.all='inherit'
                dragbuffer.innerHTML='';
                dragbuffer.appendChild(dragEle);
                inheritStyles(element,dragEle)

                const childStyle = window.getComputedStyle(dragEle);
                let value = childStyle.getPropertyValue('background-color');
                if (value==='transparent'||value==="rgba(0, 0, 0, 0)"){
                    dragEle.style.setProperty('background-color',
                        window.getComputedStyle(dragbuffer).getPropertyValue('background-color'));
                }
                value = childStyle.getPropertyValue('border-radius');
                if (value==='0px'||value==="none"){
                    dragEle.style.setProperty('border-radius',
                        window.getComputedStyle(dragbuffer).getPropertyValue('border-radius'));
                }
                if (element)
                dragEle.style.overflow='hide'
                // dragEle.style.display='flex'

                dragEle.style.minWidth=element.clientWidth+'px'
                dragEle.style.minHeight=element.clientHeight+'px'
                yPosition= startY-(element.clientHeight/2)//-40//+(element.clientHeight/3);
                xPosition=startX//+(element.clientWidth/2)//+navPara
                dragEle.style.transform = 'translate3d(' + xPosition + 'px, ' + yPosition + 'px, 0)';



                // document.body.style.overflow='hide'
                return false
            }
        }

        if (e.cancelable) {
            e.preventDefault(); // 确保只在事件可取消时调用

            // 执行其他逻辑...
        }else {
            // cancel()
            console.log("不可取消事件"+e.event)
            console.log("不可取消事件"+e.target)
            return false
        }
        e.stopPropagation()
        // 计算拖动的距离
        let deltaX = e.touches[0].clientX - startX+xPosition;
        let deltaY = e.touches[0].clientY - startY+yPosition;
        // console.log("deltaX"+deltaX)
        // console.log("deltaY"+deltaY)
        // 在这里处理拖动逻辑，比如更新元素位置
        dragEle.style.transform = 'translate3d(' + deltaX + 'px, ' + deltaY + 'px, 0)';

        // 更新开始位置，以便计算接下来的移动距离
        touchX=e.touches[0].clientX
        touchY=e.touches[0].clientY

        let len=0
        function ft(){
            id=setTimeout(()=>{

                if (funMov(e)===undefined){
                    len=touchScroll(e,len)
                }
                ft()
            },time/2)
        }
        ft()
    }

    function cancel(e) {
        if (dragEle==null){
            isDragging = false;
            return false
        }
        isDragging = false;
        dragbuffer.removeChild(dragEle)
        dragEle=null;
        ele.style.overflow=null
        clearTimeout(id)
    }
    function end(e) {
        clearTimeout(id)
        // e.preventDefault()
        // e.stopPropagation()
        // 标记为不再拖动
        ele.style.overflow=null
        if (dragEle==null){
            isDragging = false;
            if(longTouch){
                try {
                    let at=touchesStart[0]
                    let x=at.clientX
                    let y=at.clientY
                    funLong(x,y)
                    e.preventDefault()
                    e.stopPropagation()
                }catch (ecp){
                    console.log(ecp)
                }
            }
            return false
        }
        let touch = e.touches[0]; // 获取第一个触摸点
        console.log("touch"+touch)
        // 获取触摸点在页面上的位置
        let element = document.elementFromPoint(touchX, touchY);
        // element=getDragUnder(element,touchX,touchY)
        // element=getUnder(element,touchX,touchY)

        // let sct=isElementInsideRightUlLi(element)

        try {
            fun(e,dragEle)
            e.preventDefault()
            e.stopPropagation()
        }catch (ecp){
            console.log(ecp)
        }

        isDragging = false;
        dragbuffer.removeChild(dragEle)
        dragEle=null;
        e.target.style.removeProperty('--my-tm','sy')
    }

    ele.addEventListener('touchstart',start)
    ele.childNodes.forEach((v,k)=>{
        v.addEventListener('touchmove',move, { passive: false })
    })
    // ele
    ele.addEventListener('touchcancel',cancel)
    ele.addEventListener('touchend',end)
    ele.addEventListener('customClearT',function (){
        ele.removeEventListener('touchstart',start)
        ele.childNodes.forEach((v,k)=>{
            v.removeEventListener('touchmove',move)
        })
        // ele.removeEventListener('touchmove',move)
        ele.removeEventListener('touchcancel',cancel)
        ele.removeEventListener('touchend',end)
        clearTimeout(id)
    })
}

function addDragMouseParent(ele,fun,funMov,funLong,startCla=[""],
                            dragbuffer=document.getElementById('dragbuffer'),
                            matches,time) {
    ele.dispatchEvent(customEvent)
    let isDragging=false
    let id;
    let target
    let xPosition
    let yPosition
    let dragEle
    let touchX,touchY
    let startX,startY
    let startTime
    let longMouse=false
    let mouseStartEle
    let mouseStarx
    let mouseStary
    function start(e) {

        // 标记为正在拖动
        id=setTimeout(()=>{
            isDragging=false
            // let x=e.clientX
            // let y=e.clientY
            // funLong(x,y)
        // },time)
            longMouse=true
        // funLong(x,y)
        },time)
        mouseStartEle=e.target
        mouseStarx=e.clientX
        mouseStary=e.clientY
        isDragging = true;
        startTime=Date.now()
        // 获取当前触摸点的坐标
        return false
    }

    function move(e) {
        clearTimeout(id)
        // 如果不是拖动状态，则不处理
        if (!isDragging) {
            return
        } else {
            if (dragEle==null){
                if ((Date.now()-startTime)<300){
                    return
                }
                // 获取点
                // const targetElement = document.elementFromPoint(e.clientX, e.clientY);
                //
                // if (mouseStartEle.contains(targetElement)) {
                //     console.log('触摸已离开目标元素');
                //     return
                //     // 可以在这里触发自定义逻辑（如取消选中状态）
                // }

                target=e.target
                console.log("touchstartdrag")
                // 记录开始拖动时的位置
                startX = e.clientX;
                startY = e.clientY;
                let element=e.target

                while (element){
                    if (startCla.some(className => element.classList.contains(className))){
                        break
                    }else {
                        if (!ele.contains(element)){
                            return;
                        }else {
                            element=element.parentNode
                        }
                    }
                }

                dragEle=element.cloneNode(true)
                let vc=document.getElementById('viewFileCon');
                dragEle.dataset.name=vc.dataset.name
                dragEle.style.setProperty('--my-tm','sy')
                dragbuffer.innerHTML='';
                dragbuffer.appendChild(dragEle);
                inheritStyles(element,dragEle)

                const childStyle = window.getComputedStyle(dragEle);
                let value = childStyle.getPropertyValue('background-color');
                if (value==='transparent'||value==="rgba(0, 0, 0, 0)"){
                    dragEle.style.setProperty('background-color',
                        window.getComputedStyle(dragbuffer).getPropertyValue('background-color'));
                }
                value = childStyle.getPropertyValue('border-radius');
                if (value==='0px'||value==="none"){
                    dragEle.style.setProperty('border-radius',
                        window.getComputedStyle(dragbuffer).getPropertyValue('border-radius'));
                }
                dragEle.style.setProperty('overflow','hide')

                dragEle.style.minWidth=element.clientWidth+'px'
                dragEle.style.minHeight=element.clientHeight+'px'

                // yPosition= startY-40+(element.clientHeight/3);
                // xPosition=startX-79+(element.clientWidth/2)//+navPara

                yPosition= startY-(element.clientHeight/2)//-40//+(element.clientHeight/3);
                xPosition=startX//+(element.clientWidth/2)//+navPara
                dragEle.style.transform = 'translate3d(' + xPosition + 'px, ' + yPosition + 'px, 0)';

                dragEle.addEventListener('mousemove',move)
                // ele.addEventListener('mouseleave',cancel)
                dragEle.addEventListener('mouseup',end)
                touchX=e.clientX
                touchY=e.clientY
                return
            }
        }

        // 阻止默认的触摸行为，比如滚动
        // e.preventDefault();

        // 计算拖动的距离
        let deltaX = e.clientX - startX+xPosition;
        let deltaY = e.clientY - startY+yPosition;
        // console.log("deltaX"+deltaX)
        // console.log("deltaY"+deltaY)
        // 在这里处理拖动逻辑，比如更新元素位置
        dragEle.style.transform = 'translate3d(' + deltaX + 'px, ' + deltaY + 'px, 0)';
        dragEle.style.display='flex'
        // 更新开始位置，以便计算接下来的移动距离
        touchX=e.clientX
        touchY=e.clientY
        let len=0
        function ft(){
            id=setTimeout(()=>{
                if (funMov(e)===undefined){
                    len=mouseScroll(e,len)
                }
                ft()
            },time/2)
        }
        ft()
    }

    function cancel(e) {
        console.log("cancel")
        clearTimeout(id)
        if (dragEle==null){
            isDragging = false;
            return false
        }
        if (ele.contains(e.target)){
            return false
        }
        isDragging=false
        dragbuffer.removeChild(dragEle)
        dragEle=null;
    }

    function end(e) {
        clearTimeout(id)
        // if (dragEle==null){
        //     isDragging = false;
        //     return false
        // }
        if((Math.abs(e.clientY-mouseStarx)+Math.abs(e.clientX-mouseStary))>10){
            e.stopPropagation()
            e.preventDefault()
        }
        if (dragEle==null){
            isDragging = false;
            if(longMouse){
                try {
                    funLong(mouseStarx,mouseStary)

                }catch (ecp){
                    console.log(ecp)
                }
                longMouse=false
            }
            return false
        }
        // 标记为不再拖动
        console.log("mouse")
        // 获取触摸点在页面上的位置
        let element = document.elementFromPoint(touchX, touchY);
        element=getDragUnder(element,touchX,touchY)
        // element=getUnder(element,touchX,touchY)
        // scrollToEle(dragEle)
        let sct=isElementInsideRightUlLi(element)
        try {
            fun(e,dragEle)
            // e.stopPropagation()
            // e.preventDefault()
        }catch (ecp){
            console.log(ecp)
        }
        isDragging = false;
        dragbuffer.removeChild(dragEle)
        dragEle=null;
    }

    ele.addEventListener('mousedown',start)
    ele.addEventListener('mousemove',move)
    ele.addEventListener('mouseout',cancel)
    // ele.addEventListener('mouseleave',cancel)
    ele.addEventListener('mouseup',end)
    ele.addEventListener('customClear',function (){
        ele.removeEventListener('mousedown',start)
        ele.removeEventListener('mousemove',move)
        ele.removeEventListener('mouseout',cancel)
        ele.removeEventListener('mouseup',end)
        clearTimeout(id)
    })
}

function getDragsUnder(x,y) {
    // 临时隐藏顶层元素
    let view=dragbuffer.style.visibility;
    dragbuffer.style.visibility = 'hidden';
    // 获取隐藏顶层元素后露出的元素
    let belowElement = document.elementFromPoint(x, y);
    // 恢复顶层元素的可见性
    // e.style.visibility = 'visible';
    dragbuffer.style.visibility = view;
    // 返回下层元素
    return belowElement;
}

function touchScroll(e,len) {
    const screenWidth = window.innerWidth;
    const screenHeight = window.innerHeight;

    // 计算屏幕边缘10%区域的边界
    const edgeWidth = screenWidth * 0.1;
    const edgeHeight = screenHeight * 0.1;

    // 获取鼠标的屏幕坐标
    const touchX=e.touches[0].clientX
    // const touchY=e.touches[0].clientY

    // 检查鼠标是否在屏幕边缘的10%区域内
    //      || // 左侧边缘
    //      || // 右侧边缘
    //     mouseY <= edgeHeight || // 顶部边缘
    //     mouseY >= screenHeight - edgeHeight // 底部边缘
    if(touchX <= edgeWidth){
        len=len-50
        scrollNum(len)
    }
    if(touchX >= screenWidth - edgeWidth){
        len=len+50
        scrollNum(len)
    }
    return len
}

function mouseScroll(e,len) {
    const screenWidth = window.innerWidth;
    const screenHeight = window.innerHeight;

    // 计算屏幕边缘10%区域的边界
    const edgeWidth = screenWidth * 0.1;
    const edgeHeight = screenHeight * 0.1;

    // 获取鼠标的屏幕坐标
    const mouseX = e.clientX;
    const mouseY = e.clientY;

    // 检查鼠标是否在屏幕边缘的10%区域内
    //
    //      || // 左侧边缘
    //      || // 右侧边缘
    //     mouseY <= edgeHeight || // 顶部边缘
    //     mouseY >= screenHeight - edgeHeight // 底部边缘

    if(mouseX <= edgeWidth){
        len=len-50
        scrollNum(len)
    }
    if(mouseX >= screenWidth - edgeWidth){
        len=len+50
        scrollNum(len)
    }
    return len
}


function addDragLongTouchParent(ele,clickFun,funMov,touchLongFun,startCla=[""],
                            dragbuffer=document.getElementById('dragbuffer'),
                            matches,time=1000) {
    ele.dispatchEvent(customEventT)
    let isDragging=false
    let id;
    let target
    let xPosition
    let yPosition
    let dragEle
    let touchEle
    let touchX,touchY
    let startX,startY
    let startTime
    let longTouch
    let touchesStart
    let touchStartEle
    let startEle
    function start(e) {
        e.stopPropagation()
        id=setTimeout(()=>{
            isDragging=false
            longTouch=true
            isDragging = true;
        },time)
        touchStartEle=e.target
        touchesStart=e.touches
        startTime=Date.now()
        // 标记为正在拖动
        longTouch=false
        startEle=e.target
        // e.target.parentNode.style.setProperty('touch-action','none')
        // e.target.parentNode.style.setProperty('overflow','hide')
        return false
    }

    function move(e) {
        clearTimeout(id)
        if (!isDragging) {return}
        else {
            if (dragEle==null){
                if ((Date.now()-startTime)<300){
                    return
                }
                const touch = e.touches[0]; // 获取第一个触摸点
                startEle = document.elementFromPoint(touch.clientX, touch.clientY);

                // if (touchStartEle.contains(targetElement)) {
                //     console.log('触摸已离开目标元素');
                //     return
                //     // 可以在这里触发自定义逻辑（如取消选中状态）
                // }
                touchX=e.touches[0].clientX
                touchY=e.touches[0].clientY
                target=e.target
                console.log("touchstartdrag")
                // 记录开始拖动时的位置
                startX = e.touches[0].clientX;
                startY = e.touches[0].clientY;
                let element=e.target
                element.style.setProperty('--my-tm','sy')
                element.style.setProperty('--my-sy','sy')
                // element.parentNode.style.setProperty('touch-action','none')

                // while (!element.classList.contains(startCla)){
                // let b=startCla.some(className => element.classList.contains(className))
                // while (element){
                //     if (startCla.some(className => element.classList.contains(className))){
                //         break
                //     }else {
                //         if (!ele.contains(element)){
                //             return;
                //         }else {
                //             element=element.parentNode
                //         }
                //     }
                // }
                touchEle=element
                dragEle=element.cloneNode(true)
                // let eleClass = element.classList[0]
                // dragEle.addClass(eleClass)
                let vc=document.getElementById('viewFileCon');
                dragEle.dataset.name=vc.dataset.name

                // dragEle.style=element.style
                // dragEle.style.all='inherit'
                dragbuffer.innerHTML='';
                dragbuffer.appendChild(dragEle);
                inheritStyles(element,dragEle)

                const childStyle = window.getComputedStyle(dragEle);
                let value = childStyle.getPropertyValue('background-color');
                if (value==='transparent'||value==="rgba(0, 0, 0, 0)"){
                    dragEle.style.setProperty('background-color',
                        window.getComputedStyle(dragbuffer).getPropertyValue('background-color'));
                }
                value = childStyle.getPropertyValue('border-radius');
                if (value==='0px'||value==="none"){
                    dragEle.style.setProperty('border-radius',
                        window.getComputedStyle(dragbuffer).getPropertyValue('border-radius'));
                }
                if (element)
                    dragEle.style.overflow='hide'
                // dragEle.style.display='flex'
                let l = Math.min(element.clientWidth,element.clientHeight)
                dragEle.style.minWidth=l+'px'
                dragEle.style.minHeight=l+'px'
                yPosition= startY-(element.clientHeight)//-40//+(element.clientHeight/3);
                xPosition=startX//+(element.clientWidth/2)//+navPara
                dragEle.style.transform = 'translate3d(' + xPosition + 'px, ' + yPosition + 'px, 0)';

                // document.body.style.overflow='hide'
                return false
            }
        }

        if (e.cancelable) {
            e.preventDefault(); // 确保只在事件可取消时调用

            // 执行其他逻辑...
        }else {
            // cancel()
            // console.log("不可取消事件"+e.event)
            // console.log("不可取消事件"+e.target)
            // return false
        }
        e.stopPropagation()
        // 计算拖动的距离
        let deltaX = e.touches[0].clientX - startX+xPosition;
        let deltaY = e.touches[0].clientY - startY+yPosition;
        // console.log("deltaX"+deltaX)
        // console.log("deltaY"+deltaY)
        // 在这里处理拖动逻辑，比如更新元素位置
        dragEle.style.transform = 'translate3d(' + deltaX + 'px, ' + deltaY + 'px, 0)';

        // 更新开始位置，以便计算接下来的移动距离
        touchX=e.touches[0].clientX
        touchY=e.touches[0].clientY

        let len=0
        function ft(){
            id=setTimeout(()=>{

                if (funMov(e)===undefined){
                    len=touchScroll(e,len)
                }
                ft()
            },time/2)
        }
        ft()
    }

    function cancel(e) {
        if (dragEle==null){
            isDragging = false;
            return false
        }
        isDragging = false;
        dragbuffer.removeChild(dragEle)
        dragEle=null;
        clearTimeout(id)
    }
    function end(e) {
        clearTimeout(id)
        // e.preventDefault()
        // e.stopPropagation()
        // 标记为不再拖动
        if (isDragging){
            isDragging = false;
            if(longTouch){
                try {
                    let at=e.changedTouches[0]
                    let x=at.clientX
                    let y=at.clientY

                    touchLongFun(startEle,x,y)
                    e.preventDefault()
                    e.stopPropagation()
                }catch (ecp){
                    console.log(ecp)
                }
            }

            dragbuffer.removeChild(dragEle)
            dragEle=null;
            return false
        }else {
            let touch = e.changedTouches[0]; // 获取第一个触摸点
            console.log("touch"+touch)
            // 获取触摸点在页面上的位置
            let element = document.elementFromPoint(touch.clientX, touch.clientY);

            // element=getDragUnder(element,touchX,touchY)
            // element=getUnder(element,touchX,touchY)
            // let sct=isElementInsideRightUlLi(element)
            if(element===startEle){
                try {
                    clickFun(e,startEle)
                    e.preventDefault()
                    e.stopPropagation()
                }catch (ecp){
                    console.log(ecp)
                }
            }
        }

        isDragging = false;
        e.target.style.removeProperty('--my-tm','sy')
    }

    ele.addEventListener('touchstart',start, { passive: false })
    ele.childNodes.forEach((v,k)=>{
        v.addEventListener('touchmove',move, { passive: false })
    })
    // ele
    ele.addEventListener('touchcancel',cancel)
    ele.addEventListener('touchend',end)
    // ele.addEventListener('customClearT',function (){
    //     ele.removeEventListener('touchstart',start)
    //     ele.childNodes.forEach((v,k)=>{
    //         v.removeEventListener('touchmove',move)
    //     })
    //     // ele.removeEventListener('touchmove',move)
    //     ele.removeEventListener('touchcancel',cancel)
    //     ele.removeEventListener('touchend',end)
    //     clearTimeout(id)
    // })

    //
    //
    // function handleStart(event) {
    //     if (event.cancelable) event.preventDefault(); // 阻止默认行为（如滚动）
    //     start(event)
    //     // console.log('开始:', event.type);
    // }
    // function handleMove(event) {
    //     if (event.cancelable) event.preventDefault(); // 阻止默认行为（如滚动）
    //     move(event)
    // }
    // function handleCancel(event) {
    //     if (event.cancelable) event.preventDefault(); // 阻止默认行为（如滚动）
    //     cancel(event)
    // }
    // function handleEnd(event) {
    //     if (event.cancelable) event.preventDefault(); // 阻止默认行为（如滚动）
    //     end(event)
    // }
    //
    // ele.addEventListener('touchstart',handleStart, { passive: false })
    // ele.childNodes.forEach((v,k)=>{
    //     v.addEventListener('touchmove',handleMove, { passive: false })
    // })
    //
    // ele.addEventListener('touchcancel',handleCancel)
    // ele.addEventListener('touchend',handleEnd)
    // ele.addEventListener('customClearT',function (){
    //     ele.removeEventListener('touchstart',handleStart)
    //     ele.childNodes.forEach((v,k)=>{
    //         v.removeEventListener('touchmove',handleMove)
    //     })
    //     // ele.removeEventListener('touchmove',move)
    //     ele.removeEventListener('touchcancel',handleCancel)
    //     ele.removeEventListener('touchend',handleEnd)
    //     clearTimeout(id)
    // })
}

function addDragLongMouseParent(ele,clickFun,funMov,mouseLongFun,startCla=[""],
                                dragbuffer=document.getElementById('dragbuffer'),
                                matches,time=1000) {
    ele.dispatchEvent(customEventT)
    let isDragging=false
    let id;
    let target
    let xPosition
    let yPosition
    let dragEle
    let touchEle
    let startX,startY
    let startTime
    let longTouch
    let mouseStarx
    let mouseStary
    let mouseStartEle
    let startEle
    function start(e) {
        id=setTimeout(()=>{
            isDragging=false
            longTouch=true
            isDragging = true;
        },time)
        mouseStartEle=e.target
        mouseStarx=e.clientX
        mouseStary=e.clientY
        startTime=Date.now()
        // 标记为正在拖动
        longTouch=false
        startEle=e.target
        // e.target.parentNode.style.setProperty('touch-action','none')
        // e.target.parentNode.style.setProperty('overflow','hide')
        return false
    }

    function move(e) {
        clearTimeout(id)
        if (!isDragging) {return}
        else {
            if (dragEle==null){
                if ((Date.now()-startTime)<300){
                    return
                }
                startEle = document.elementFromPoint(e.clientX, e.clientY);

                // if (touchStartEle.contains(targetElement)) {
                //     console.log('触摸已离开目标元素');
                //     return
                //     // 可以在这里触发自定义逻辑（如取消选中状态）
                // }
                target=e.target
                console.log("touchstartdrag")
                // 记录开始拖动时的位置
                startX = e.clientX;
                startY = e.clientY;
                let element=e.target
                element.style.setProperty('--my-tm','sy')
                touchEle=element
                dragEle=element.cloneNode(true)
                // let eleClass = element.classList[0]
                // dragEle.addClass(eleClass)
                let vc=document.getElementById('viewFileCon');
                dragEle.dataset.name=vc.dataset.name

                // dragEle.style=element.style
                // dragEle.style.all='inherit'
                dragbuffer.innerHTML='';
                dragbuffer.appendChild(dragEle);
                inheritStyles(element,dragEle)

                const childStyle = window.getComputedStyle(dragEle);
                let value = childStyle.getPropertyValue('background-color');
                if (value==='transparent'||value==="rgba(0, 0, 0, 0)"){
                    dragEle.style.setProperty('background-color',
                        window.getComputedStyle(dragbuffer).getPropertyValue('background-color'));
                }
                value = childStyle.getPropertyValue('border-radius');
                if (value==='0px'||value==="none"){
                    dragEle.style.setProperty('border-radius',
                        window.getComputedStyle(dragbuffer).getPropertyValue('border-radius'));
                }
                if (element)
                    dragEle.style.overflow='hide'
                // dragEle.style.display='flex'
                let l = Math.min(element.clientWidth,element.clientHeight)
                dragEle.style.minWidth=l+'px'
                dragEle.style.minHeight=l+'px'
                yPosition= startY-(element.clientHeight)//-40//+(element.clientHeight/3);
                xPosition=startX//+(element.clientWidth/2)//+navPara
                dragEle.style.transform = 'translate3d(' + xPosition + 'px, ' + yPosition + 'px, 0)';

                dragEle.style.transform = 'translate3d(' + xPosition + 'px, ' + yPosition + 'px, 0)';

                dragEle.addEventListener('mousemove',move)
                // ele.addEventListener('mouseleave',cancel)
                dragEle.addEventListener('mouseup',end)
                return
            }
        }

        if (e.cancelable) {
            e.preventDefault(); // 确保只在事件可取消时调用
            // 执行其他逻辑...
        }else {
            // cancel()
            console.log("不可取消事件"+e.event)
            console.log("不可取消事件"+e.target)
            return false
        }
        e.stopPropagation()
        // 计算拖动的距离
        let deltaX = e.clientX - startX+xPosition;
        let deltaY = e.clientY - startY+yPosition;
        // console.log("deltaX"+deltaX)
        // console.log("deltaY"+deltaY)
        // 在这里处理拖动逻辑，比如更新元素位置
        dragEle.style.transform = 'translate3d(' + deltaX + 'px, ' + deltaY + 'px, 0)';

        // 更新开始位置，以便计算接下来的移动距离
        touchX=e.clientX
        touchY=e.clientY

        let len=0
        function ft(){
            id=setTimeout(()=>{
                if (funMov(e)===undefined){
                    len=mouseScroll(e,len)
                }
                ft()
            },time/2)
        }
        ft()
    }

    function cancel(e) {
        console.log("cancel")
        clearTimeout(id)
        if (dragEle==null){
            isDragging = false;
            return false
        }
        if (ele.contains(e.target)){
            return false
        }
        isDragging=false
        dragbuffer.removeChild(dragEle)
        dragEle=null;
    }

    function end(e) {
        clearTimeout(id)
        // e.preventDefault()
        // e.stopPropagation()
        // 标记为不再拖动
        if (isDragging){
            isDragging = false;
            if(longTouch){
                try {

                    let x=e.clientX
                    let y=e.clientY

                    mouseLongFun(startEle,x,y)
                    // e.preventDefault()
                    // e.stopPropagation()
                }catch (ecp){
                    console.log(ecp)
                }
            }
            dragbuffer.removeChild(dragEle)
            dragEle=null;
            return false
        }else {
            // 获取触摸点在页面上的位置
            let element = document.elementFromPoint(e.clientX, e.clientY);

            // element=getDragUnder(element,touchX,touchY)
            // element=getUnder(element,touchX,touchY)
            // let sct=isElementInsideRightUlLi(element)
            if(element===startEle){
                try {
                    clickFun(e,startEle)
                    // e.preventDefault()
                    // e.stopPropagation()
                }catch (ecp){
                    console.log(ecp)
                }
            }
        }

        isDragging = false;
        e.target.style.removeProperty('--my-tm','sy')
    }

    ele.addEventListener('mousedown',start)
    ele.addEventListener('mousemove',move)
    ele.addEventListener('mouseout',cancel)
    // ele.addEventListener('mouseleave',cancel)
    ele.addEventListener('mouseup',end)
    ele.addEventListener('customClear',function (){
        ele.removeEventListener('mousedown',start)
        ele.removeEventListener('mousemove',move)
        ele.removeEventListener('mouseout',cancel)
        ele.removeEventListener('mouseup',end)
        clearTimeout(id)
    })

    // ele.addEventListener('pointerdown',start)
    // ele.addEventListener('pointermove',move)
    // ele.addEventListener('pointerout',cancel)
    // // ele.addEventListener('mouseleave',cancel)
    // ele.addEventListener('pointerup',end)
    // ele.addEventListener('customClear',function (){
    //     ele.removeEventListener('pointerdown',start)
    //     ele.removeEventListener('pointermove',move)
    //     ele.removeEventListener('pointerout',cancel)
    //     ele.removeEventListener('pointerup',end)
    //     clearTimeout(id)
    // })
}

function dragBoxScroll(e,len) {
    const screenWidth = window.innerWidth;
    const screenHeight = window.innerHeight;

    // 计算屏幕边缘10%区域的边界
    const edgeWidth = screenWidth * 0.1;
    // 获取鼠标的屏幕坐标
    const mouseX = e.touches[0].clientX;

    if(mouseX <= edgeWidth){
        len=len-100
        scrollNum(len)
    }
    if(mouseX >= screenWidth - edgeWidth){
        len=len+100
        scrollNum(len)
    }
}
