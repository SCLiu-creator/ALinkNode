function cheakAndFindFatherClass(element,name) {
    while (element.className!== name){
        element=element.parentNode;
        if (element===null){
            return element;
        }
    }
    return element;
}

function scrollNum(margin=0) {
    let targetElement=document.querySelector('.rightbody')
    const offsetLeft = targetElement.scrollLeft;
    // containerWheel.scrollLeft =offsetLeft+ margin;
    smoothScroll(containerWheel, offsetLeft+ margin, 1000)
    console.log("scrollToEle "+targetElement)
}
function smoothScroll(element, targetScrollLeft, duration) {
    const startScrollLeft = element.scrollLeft;
    const startTime = performance.now();

    function scrollStep(currentTime) {
        const timeElapsed = currentTime - startTime;
        const progress = Math.min(timeElapsed / duration, 1);
        const scrollLeft = startScrollLeft + (targetScrollLeft - startScrollLeft) * easeInOutQuad(progress);
        element.scrollLeft = scrollLeft;

        if (progress < 1) {
            requestAnimationFrame(scrollStep);
        }
    }
    function easeInOutQuad(t) {
        return t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t;
    }
    requestAnimationFrame(scrollStep);
}

function scrollToEle(targetElement,margin=0) {
    const offsetLeft = targetElement.offsetLeft;
    containerWheel.scrollLeft = offsetLeft-81 +navPara+margin;
    console.log("scrollToEle "+targetElement)
}

function smoothScrollTo(element, targetPosition, duration ,paEle=containerWheel) {
    const startPosition = element.offsetLeft;
    const startTime = performance.now();
    function scrollStep(currentTime) {
        const timeElapsed = currentTime - startTime;
        const run = ease(timeElapsed, startPosition, targetPosition - startPosition, duration);
        paEle.scrollLeft = run;

        if (timeElapsed < duration) {
            requestAnimationFrame(scrollStep);
        }
    }
    function ease(t, b, c, d) {
        // 使用 easeInOutQuad 缓动函数
        t /= d / 2;
        if (t < 1) return c / 2 * t * t + b;
        t--;
        return -c / 2 * (t * (t - 2) - 1) + b;
    }
    requestAnimationFrame(scrollStep);
}
function smoothScrollPos(startPosition, targetPosition, duration ,paEle=containerWheel) {
    const startTime = performance.now();
    function scrollStep(currentTime) {
        const timeElapsed = currentTime - startTime;
        const run = easeInOutExpo(timeElapsed, startPosition, targetPosition - startPosition, duration);
        paEle.scrollLeft = run;
        // console.log("scrolling")
        // console.log("timeElapsed: "+timeElapsed)
        // console.log("duration: "+duration)
        // console.log("run: "+run)
        if (timeElapsed < duration) {
            if (paEle.scrollLeft!==targetPosition){
                requestAnimationFrame(scrollStep);
            }else {
                console.log("scrollover")
            }
        }else {
            console.log("scrollover")
        }
    }

    function tim(t, b, c, d) {
        return t/d *(b+c)
        // return c / 2 * (-Math.pow(2, -10 * t) + 2) + b;
    }
    function easeInOutExpo(t, b, c, d) {
        t /= d / 2;
        if (t < 1) return c / 2 * Math.pow(2, 10 * (t - 1)) + b;
        t--;
        return c / 2 * (-Math.pow(2, -10 * t) + 2) + b;
    }
    function linear(t, b, c, d) {
        return c * t / d + b;
    }
    function ease(t, b, c, d) {
        // 使用 easeInOutQuad 缓动函数
        t /= d / 2;
        if (t < 1) return c / 2 * t * t + b;
        t--;
        return -c / 2 * (t * (t - 2) - 1) + b;
    }
    // function ease(t, b, c, d, epsilon = 0.001) {
    //     // 使用 easeInOutQuad 缓动函数
    //     t /= d / 2;
    //     if (t < 1) {
    //         return c / 2 * t * t + b;
    //     } else {
    //         t--;
    //         let result = -c / 2 * (t * (t - 2) - 1) + b;
    //         // 如果结果和最终值之差小于阈值，则返回最终值
    //         if (Math.abs(result - (b + c)) < epsilon) {
    //             return b + c;
    //         }
    //         return result;
    //     }
    // }
    requestAnimationFrame(scrollStep);
}


function showText(text) {
    let body=document.querySelector(".create")
    let ssss=document.createElement("div");
    ssss.setAttribute("class","textbox");
    ssss.innerText=text
    body.appendChild(ssss);
    window.setTimeout(function () {
        body.removeChild(ssss);
    },1500)
}
function showText1(text) {
    let body=document.querySelector("BODY")
    let ssss=document.createElement("div")
    ssss.setAttribute("class","textbox1");
    ssss.innerText=text
    body.appendChild(ssss);
    window.setTimeout(function () {
        body.removeChild(ssss);
    },2000)
}
function UnDisplayEleChildren(element) {
    for (let node of element.childNodes){
        node.style.display='none'
    }
}

let loadc;
function viewLoadShow(e) {
    let show = document.querySelector("#showFile")
    loadc=show.querySelector("#loadCon")
    if (loadc==null){
        loadc=document.createElement('div');
        loadc.setAttribute('id','loadCon')
        let loadv=document.createElement('div');
        loadv.setAttribute('class','loadv')
        let loadr=document.createElement('div');
        loadr.setAttribute('class','loadr')
        loadv.appendChild(loadr)
        loadc.appendChild(loadv)
        show.appendChild(loadc)
        let parentWidth = show.offsetWidth;
        let parentHeight = show.offsetHeight;
        if (parentWidth>parentHeight){
            loadr.style.width = parentHeight*0.46 + 'px';
            loadr.style.height = parentHeight*0.46 + 'px';
        }else {
            loadr.style.width = parentWidth*0.46 + 'px';
            loadr.style.height = parentWidth*0.46 + 'px';
        }
        loadv.style.width = parentWidth + 'px';
        loadv.style.height = parentHeight + 'px';
    }
}
function viewLoadcl(e) {
    let show = document.querySelector("#showFile")
    loadc=show.querySelector("#loadCon")
    if (loadc!=null){
        loadc.parentNode.removeChild(loadc)
    }
}

function getDragUnder(e,x,y) {
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

function showDig(str) {
    let body=document.querySelector(".create")
    let ssss=document.createElement("div")
    ssss.setAttribute("class","textbox1");
    ssss.innerText=str
    body.appendChild(ssss);
    window.setTimeout(function () {
        body.removeChild(ssss);
    },1500)
}

//绑定长按事件
function enableLongPress(element, callback=(e)=>{},callClick=(e)=>{}, duration = 1000) {
    let pressTimer = null;
    let startTime = null;
    let isInside = false;
    let reCall = false;

    // 启动长按检测
    const start = (event) => {
        startTime = Date.now(); // 记录按下时间
        isInside = true; // 标记指针在元素内

        // 设置定时器（用于自动触发，但会检查指针位置）
        pressTimer = setTimeout(() => {
            if (isInside) {
                // callback(event); // 仅在指针未移出时触发
                reCall=true
            }
        }, duration);
    };

    // 取消长按检测
    const cancel = () => {
        clearTimeout(pressTimer);
        pressTimer = null;
        isInside = false; // 标记指针已离开
    };

    // 抬起时检查条件
    const checkAndTrigger = (event) => {
        if (pressTimer) {
            const timeDiff = Date.now() - startTime;
            // 同时满足时间条件和指针未移出
            if (timeDiff >= duration ) {
                if (reCall && isInside){
                    event.preventDefault();
                    console.log('长按菜单已阻止');
                    callback(event);
                }
            }else {
                if(isInside){
                    // event.preventDefault();
                    // console.log('长按菜单已阻止');
                    callClick(event)
                }
            }
        }
        reCall=false
        cancel(); // 清理状态
    };

    // 鼠标事件绑定
    element.addEventListener('mousedown', start);
    element.addEventListener('mouseup', checkAndTrigger);
    element.addEventListener('mouseout', cancel); // 移出时取消

    // 触摸事件绑定（注意 touchstart 使用 passive: true 优化性能）
    element.addEventListener('touchstart', start, { passive: true });
    element.addEventListener('touchend', checkAndTrigger);
    element.addEventListener('touchcancel', cancel); // 系统取消触摸时取消

    // 返回清理函数
    return () => {
        cancel(); // 确保清理状态
        // 移除所有事件监听
        element.removeEventListener('mousedown', start);
        element.removeEventListener('mouseup', checkAndTrigger);
        element.removeEventListener('mouseout', cancel);
        element.removeEventListener('touchstart', start);
        element.removeEventListener('touchend', checkAndTrigger);
        element.removeEventListener('touchcancel', cancel);
    };
}

/**
 * 判断元素是否完全在视口内
 * @param {HTMLElement} element - 要检查的元素
 * @returns {boolean} - 如果元素完全在视口内返回true，否则返回false
 */
function isElementFullyInViewport(element) {
    // 获取元素的边界矩形
    const rect = element.getBoundingClientRect();

    // 获取视口的尺寸
    const viewportWidth = window.innerWidth || document.documentElement.clientWidth;
    const viewportHeight = window.innerHeight || document.documentElement.clientHeight;

    // 检查元素是否完全在视口内
    return (
        rect.top >= 0 &&
        rect.left >= 0 &&
        rect.bottom <= viewportHeight &&
        rect.right <= viewportWidth
    );
}

/**
 * 判断元素在视口中的显示比例是否大于指定阈值
 * @param {HTMLElement} element - 要检查的元素
 * @param {number} threshold - 显示比例阈值（0到1之间）
 * @param {function} callback - 回调函数，接收是否满足阈值的结果
 * @returns {IntersectionObserver} - 返回IntersectionObserver实例以便后续控制
 */
function checkVisibilityThreshold(element, threshold, callback) {
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            // 检查元素的显示比例是否大于等于阈值
            const isAboveThreshold = entry.intersectionRatio >= threshold;
            callback(isAboveThreshold, entry.intersectionRatio);
        });
    }, {
        threshold: threshold // 设置阈值
    });

    observer.observe(element);
    return observer;
}

/**
 * 计算元素在视口中的显示比例
 * @param {HTMLElement} element - 要检查的元素
 * @returns {number} - 显示比例（0到1之间）
 */
function getVisibilityRatio(element) {
    const rect = element.getBoundingClientRect();
    const viewportWidth = window.innerWidth || document.documentElement.clientWidth;
    const viewportHeight = window.innerHeight || document.documentElement.clientHeight;

    // 计算元素在视口中可见的面积
    const visibleWidth = Math.min(rect.right, viewportWidth) - Math.max(rect.left, 0);
    const visibleHeight = Math.min(rect.bottom, viewportHeight) - Math.max(rect.top, 0);

    // 如果元素完全不在视口内，返回0
    if (visibleWidth <= 0 || visibleHeight <= 0) {
        return 0;
    }

    // 计算可见面积与元素总面积的比例
    const elementArea = rect.width * rect.height;
    const visibleArea = visibleWidth * visibleHeight;

    return Math.min(visibleArea / elementArea, 1);
}


//获取文件名
function getBaseName(filePath) {
    // 规范化路径分隔符，将所有反斜杠替换为正斜杠（可选步骤，但有助于统一处理）
    const normalizedPath = filePath.replace(/\\+/g, '/');

    // 找到最后一个正斜杠的位置
    const lastSlashIndex = normalizedPath.lastIndexOf('/');

    // 提取并返回 basename
    return normalizedPath.substring(lastSlashIndex + 1);
}
// document.body.addEventListener('touchstart', function(event) {
//     if (event.target.matches('.long-touch-element')) {
//         // 处理逻辑...
//     }
// }, { passive: true });

// document.getElementById('itemList').addEventListener('click', function(event) {
//     // 检查是否点击的是 .item 元素
//     if (event.target && event.target.classList.contains('item')) {
//         console.log(`Clicked on ${event.target.textContent}`);
//     }
// });
function loadJS( url, callback ){

    var script = document.createElement('script'),
        fn = callback || function(){};
    script.type = 'text/javascript';
    //IE
    if(script.readyState){

        script.onreadystatechange = function(){

            if( script.readyState === 'loaded' || script.readyState === 'complete' ){
                script.onreadystatechange = null;
                fn();
            }
        };
    }else{
        //其他浏览器

        script.onload = function(){
            fn();
        };
    }

    script.src = url;
    document.getElementsByTagName('head')[0].appendChild(script);

}
function getStrLast(str) {
    // 找到最后一个“/”字符的位置
    const lastIndex = str.lastIndexOf('/');

    // 如果没有找到“/”，则返回原始字符串
    if (lastIndex === -1) {
        return str;
    }

    // 从最后一个“/”之后开始截取字符串
    return str.substring(lastIndex + 1);
}
//用法

function inheritStyles(parent, child) {
    const computedStyle = window.getComputedStyle(parent);
    const childStyle = window.getComputedStyle(child);
    const allProperties = computedStyle.length;

    for (let i = 0; i < allProperties; i++) {
        const property = computedStyle[i];
        const value = computedStyle.getPropertyValue(property);
        const valuec = childStyle.getPropertyValue(property);

        // if (value===null || value===undefined || value==='initial' || value==='auto'|| value==='none'){
        //     continue
        // }
        // if (valuec!==null && valuec!==undefined && valuec!=='initial' && valuec!=='auto'&& valuec!=='none'){
        //     continue
        // }
        // if (childStyle[i].includes('px')){
        //     console.log(childStyle[i])
        // }
        // if (property !== 'margin' && property !== 'padding' && property !== 'border') {
        if (valuec===null || valuec===undefined ||  valuec==='none'){
            if(value!=='none'){
                console.log(childStyle[i]+"   "+valuec)
                child.style.setProperty(property, value);
            }

        }
    }
}

/**
 * 判断鼠标是否处于屏幕边缘的10%区域内
 * @param {MouseEvent} event - 鼠标事件对象
 * @returns {boolean} - 如果鼠标在屏幕边缘的10%区域内，则返回true；否则返回false
 */
function isMouseOnEdge(event) {
    // 获取屏幕的宽度和高度
    const screenWidth = window.innerWidth;
    const screenHeight = window.innerHeight;

    // 计算屏幕边缘10%区域的边界
    const edgeWidth = screenWidth * 0.1;
    const edgeHeight = screenHeight * 0.1;

    // 获取鼠标的屏幕坐标
    const mouseX = event.clientX;
    const mouseY = event.clientY;

    // 检查鼠标是否在屏幕边缘的10%区域内
    return (
        mouseX <= edgeWidth || // 左侧边缘
        mouseX >= screenWidth - edgeWidth || // 右侧边缘
        mouseY <= edgeHeight || // 顶部边缘
        mouseY >= screenHeight - edgeHeight // 底部边缘
    );
}


loadJS('pakg/bundle.js',function(){
    // alert(1);
});
function isElementInViewport(el) {
    const rect = el.getBoundingClientRect();
    return (
        rect.top >= 0 &&
        rect.left >= 0 &&
        rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
        rect.right <= (window.innerWidth || document.documentElement.clientWidth)
    );
}
function isEleInViewport(el) {
    const rect = el.getBoundingClientRect();
    return (
        rect.top < window.innerHeight &&
        rect.bottom > 0 &&
        rect.left < window.innerWidth &&
        rect.right > 0
    );
}

function isElement(node) {
    return node.nodeType === Node.ELEMENT_NODE;
}

function isElementTag(node) {
    return typeof node.tagName === 'string';
}

//打开pdf  pdfjs-canvas
(
    function() {
        try {
            let el = document.getElementById('canvasWrap');
            if (!el) {
                el = document.createElement('div')
                el.id = 'canvasWrap'
                document.body.appendChild(el)
            }
            el.innerHTML = ''
            let winW = document.documentElement.clientWidth
            // 加载 pdf 资源
            let loadingTask = pdfjsLib.getDocument('https://www.lilnong.top/static/pdf/B-4-RxJS%E5%9C%A8React%E4%B8%AD%E7%9A%84%E5%BA%94%E7%94%A8-%E9%BE%99%E9%80%B8%E6%A5%A0_.pdf')
            // PDF 加载完成的回调。
            loadingTask.promise.then(function(pdf) {
                console.log('pdf', pdf)
                // 可以获取到总页数。
                let pageNum = pdf.numPages
                var _pageNum = 1;
                var renderPageToCanvas = function(pageNum, auto=false) {
                    // 获取其中的一个页面
                    pdf.getPage(pageNum).then(function(page) {
                        // you can now use *page* here
                        _pageNum = pageNum
                        // 获取原始大小的数据
                        var viewport = page.getViewport({
                            scale: 1,
                        });
                        var scale = (500 / viewport.width).toFixed(2)
                        viewport = page.getViewport({
                            scale: scale
                        });
                        var canvas = document.createElement('canvas');
                        el.appendChild(canvas)
                        var context = canvas.getContext('2d');
                        canvas.height = viewport.height;
                        canvas.width = viewport.width;

                        // 创建了一个canvas画板用来存放
                        var renderContext = {
                            canvasContext: context,
                            viewport: viewport
                        };
                        page.render(renderContext);
                        if (auto)
                            renderPageToCanvas(pageNum + 1, auto);
                    });
                }
                renderPageToCanvas(_pageNum, true);
                canvasPrev.onclick = function() {
                    renderPageToCanvas(Math.max(_pageNum - 1, 1));
                }
                canvasNext.onclick = function() {
                    renderPageToCanvas(Math.min(_pageNum + 1, pdf.numPages));
                }
            }, function(reason) {
                console.error(reason)
            })

        }catch (e) {
            console.log(e)
        }

    }
)()

// function fetchWithTimeout(fetchPromise, timeout = 10000,controller) {
//     // 创建一个 Promise，该 Promise 在指定的超时时间后拒绝
//     let state=true;
//     let id
//     let timeoutPromise = new Promise((_, reject) => {
//          id= setTimeout(() => {
//             clearTimeout(id);
//             if (state){
//                 outTimeNotic("连接请求超时",2000)
//             }
//             reject(`Fetch failed: timeout of ${timeout}ms exceeded`);
//         }, timeout);
//     });
//
//     let fetchthen=fetchPromise.then(response => {
//         state=false;
//         outTimeNotic("连接成功",1200)
//         clearTimeout(id);
//         // 如果 fetchPromise 赢了（即请求成功），则返回响应
//         return response;
//     })
//     // 使用 fetch 发起请求
//     // 使用 Promise.race 来等待 fetchPromise 或 timeoutPromise 中较快的一个
//     return Promise.race([
//         fetchthen,
//         timeoutPromise
//     ]).catch(error => {
//         controller.abort()
//         // 如果 timeoutPromise 赢了（即超时了），则抛出错误
//         throw error;
//     });
// }


function fetchWithTimeout(url, options = {}, timeout = 10000) {
    const controller = new AbortController();
    let timeoutId;

    // 避免覆盖已有的 signal
    const fetchOptions = { ...options };
    if (!fetchOptions.signal) {
        fetchOptions.signal = controller.signal;
    }

    const promise = fetch(url, fetchOptions)
        .then(response => {
            clearTimeout(timeoutId);
            return response;
        });

    const timeoutPromise = new Promise((_, reject) => {
        timeoutId = setTimeout(() => {
            controller.abort();
            reject(new Error(`Timeout: ${timeout}ms`));
        }, timeout);
    });

    return Promise.race([promise, timeoutPromise])
        .finally(() => clearTimeout(timeoutId));
}

//指定时长显示
function outTimeNotic(string,time) {
    let create=document.querySelector(".create")
    let ssss=document.createElement("div")
    ssss.setAttribute("class","textbox1");
    ssss.innerText=string
    create.appendChild(ssss);
    window.setTimeout(function () {
        create.removeChild(ssss);
    },time)
}
function name() {
    const x = function ()  {

    }
}


function areDataSetsEqual(el1, el2) {
    const dataset1 = el1.dataset;
    const dataset2 = el2.dataset;

    // 比较两个dataset对象的属性数量
    if (Object.keys(dataset1).length !== Object.keys(dataset2).length) {
        return false;
    }

    for (const key in dataset1) {
        if (dataset1[key] !== dataset2[key]) {
            // 如果属性值不相等，则两个dataset不相等
            return false;
        }
    }

    // 所有属性都相等，返回true
    return true;
}

function isElementOutOfBounds(parentElement, childElement, threshold = 0.5) {
    console.log('threshold = 0.5')
    const parentRect = parentElement.getBoundingClientRect();
    const childRect = childElement.getBoundingClientRect();
    // if (childRect.width>parentRect.width || childRect.height>parentRect.height){
    //     return {
    //         left:true ,
    //         top:true ,
    //         right:true,
    //         bottom:true
    //     };
    // }


    // Adjust for the transform property
    const style = window.getComputedStyle(childElement);
    const transform = style.transform
    //|| style.webkitTransform || style.mozTransform;
    const matrix = transform.match(/^matrix3d\((.+)\)$/);

    if (matrix) {
        const [
            m11, m12, m13, m14,
            m21, m22, m23, m24,
            m31, m32, m33, m34,
            m41, m42, m43, m44
        ] = matrix[1].split(',').map(Number);

        // Translate values
        const translateX = m14;
        const translateY = m24;

        // Scale values (assuming uniform scale for simplicity)
        const scaleX = Math.sqrt(m11 * m11 + m12 * m12);
        const scaleY = Math.sqrt(m21 * m21 + m22 * m22);

        // Adjust childRect for transform
        const adjustedChildRect = {
            left: childRect.left - translateX,
            top: childRect.top - translateY,
            width: childRect.width * scaleX,
            height: childRect.height * scaleY
        };

        // Check bounds
        const outOfBounds = {
            left:adjustedChildRect.left < parentRect.left - threshold * parentRect.width ,
            top:adjustedChildRect.top < parentRect.top - threshold * parentRect.height ,
            right:adjustedChildRect.right > parentRect.right + threshold * parentRect.width,
            bottom:adjustedChildRect.bottom > parentRect.bottom + threshold * parentRect.height
        };

        return outOfBounds;
    } else {
        // If no transform or unsupported transform, use original childRect
        const outOfBounds = {
            left:childRect.left -parentRect.left<  threshold * parentRect.width,
            top:childRect.top -parentRect.top< threshold * parentRect.height ,
            right:parentRect.right-childRect.right <  threshold * parentRect.width,
            bottom:parentRect.bottom-childRect.bottom <  threshold * parentRect.height
        };

        return outOfBounds;
    }
}

function decodeBase64ToUtf8(base64) {
    var binaryString = window.atob(base64);
    var len = binaryString.length;
    var bytes = new Uint8Array(len);
    for (var i = 0; i < len; i++) {
        bytes[i] = binaryString.charCodeAt(i);
    }
    return new TextDecoder("utf-8").decode(bytes);
}

function getScrollPositionRelativeToParent(element) {
    // 获取父元素
    var parentElement = element.parentNode;

    // 获取滚动条的水平和垂直位置
    var scrollTop = parentElement.scrollTop;
    var scrollLeft = parentElement.scrollLeft;

    // 返回一个包含滚动位置的对象
    return { scrollTop: scrollTop, scrollLeft: scrollLeft };
}

// 函数：根据提供的滚动位置恢复元素相对于其父元素的滚动
function restoreScrollPosition(element, scrollPosition) {
    // 设置滚动条的水平和垂直位置
    element.scrollTop = scrollPosition.scrollTop;
    element.scrollLeft = scrollPosition.scrollLeft;
}
function waitForMutation(targetNode, config) {
    return new Promise(resolve => {
        const observer = new MutationObserver(mutations => {
            resolve(mutations);
        });
        observer.observe(targetNode, config);
    });
}
function determineFileType(filePath,funPic,funVid,funMp3,funTxt,funFile) {
    // 获取文件后缀
    const fileExtension = filePath.split('.').pop().toLowerCase();

    // 根据后缀判断文件类型
    switch (fileExtension) {
        // 图片文件
        case 'jpg':
        case 'jpeg':
        case 'png':
        case 'gif':
        case 'bmp':
        case 'svg':
        case 'tiff':
        case 'psd':
            console.log('This is an image file.');
            funPic(filePath);
            break;

        // 视频文件
        case 'mp4':
        case 'avi':
        case 'mov':
        case 'wmv':
        case 'mkv':
        case 'flv':
        case 'webm':
        case '3gp':
        case 'mpg':
        case 'mpeg':
            console.log('This is a video file.');
            funVid(filePath);
            break;

        // 音频文件
        case 'mp3':
        case 'wav':
        case 'ogg':
        case 'flac':
        case 'm4a':
        case 'wma':
        case 'aac':
            console.log('This is an audio file.');
            funMp3(filePath);
            break;

        // 文本文件
        case 'txt':
        case 'doc':
        case 'docx':
        case 'pdf':
        case 'rtf':
        case 'odt':
        case 'log':
        case 'tex':
            console.log('This is a text file.');
            funTxt(filePath);
            break;

        // 压缩文件
        case 'zip':
        case 'rar':
        case '7z':
        case 'tar':
        case 'gz':
        case 'bz2':
        case 'xz':
            console.log('This is a compressed file.');
            funFile(filePath);
            break;

        // 演示文稿
        case 'ppt':
        case 'pptx':
        case 'odp':
            console.log('This is a presentation file.');
            funFile(filePath);
            break;

        // 表格文件
        case 'xls':
        case 'xlsx':
        case 'ods':
            console.log('This is a spreadsheet file.');
            funFile(filePath);
            break;

        // 其他格式
        default:
            console.log('This is an unknown file type.');
            funFile(filePath);
            break;
    }
}

class CancelToken {
    constructor() {
        this.promise = new Promise((resolve, reject) => {
            this.resolve = resolve;
            this.reject = reject;
        });
    }

    cancel(reason) {
        this.reject(reason);
    }
}

function fetchWithCancelToken(url, cancelToken) {
    return Promise.race([
        fetch(url),
        cancelToken.promise.then(() => Promise.reject(new Error('Fetch cancelled')))
    ]);
}

const controller = new AbortController();
const signal = controller.signal;

// fetch(url, { signal }).then(response => {
//     // 处理响应
// }).catch(e => {
//     if (e.name === 'AbortError') {
//         console.log('Fetch aborted');
//     } else {
//         console.error('Fetch error:', e);
//     }
// });
//
// // 当你需要取消请求时
// controller.abort();
// // 使用示例
// const cancelToken = new CancelToken();
// const fetchPromise = fetchWithCancelToken(url, cancelToken);
//
// // 取消请求
// cancelToken.cancel('Operation cancelled by the user.');

function addgtoch(ele,callback){
    ele.setAttribute('onouchstart',gtouchstart(callback))
    ele.setAttribute('ontouchend',gtouchend())
    ele.setAttribute('ontouchmove',gtouchmove())
    ele.setAttribute('onouchstart',gtouchstart())
}
//开始按
function gtouchstart(callback) {
    timeOutEvent = setTimeout(()=>longPress( callback), 500); //这里设置定时器，定义长按500毫秒触发长按事件，时间可以自己改，个人感觉500毫秒非常合适
    return false;
};

//手释放，如果在500毫秒内就释放，则取消长按事件，此时可以执行onclick应该执行的事件
function gtouchend() {
    clearTimeout(timeOutEvent); //清除定时器
    if (timeOutEvent != 0) {
        //这里写要执行的内容（尤如onclick事件）
        console.log("你这是点击，不是长按");
    }
    return false;
};

//如果手指有移动，则取消所有事件，此时说明用户只是要移动而不是长按
function gtouchmove() {
    clearTimeout(timeOutEvent); //清除定时器
    timeOutEvent = 0;
};

//真正长按后应该执行的内容
function longPress(id) {
    // timeOutEvent = 0;
    // //执行长按要执行的内容，如弹出菜单
    // console.log(id);
    id()
    alert("长按事件触发发");
}
