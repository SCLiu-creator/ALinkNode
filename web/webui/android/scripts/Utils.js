function getMaxEle() {
    let elements=[]
    // elements[0]=document.getElementById('dragContainer')
    elements.push(document.getElementById('dl'))
    elements.push(document.getElementById('cloudeOpertor'))
    elements.push(document.getElementById('chat'))
    elements.push(document.getElementById('content-ll'))
    elements.push(document.getElementById('content-b'))
    elements.push(document.getElementById('proxy_page'))
    elements.push(document.getElementById('picture1'))
    elements.push(document.getElementById('ToolMenu'))

    const viewportWidth = window.innerWidth; // 视口宽度
    let visibilityRatioBuf=0;
    let maxEle=null;
    elements.forEach(element => {
        const rect = element.getBoundingClientRect();
        const elementWidth = element.offsetWidth; // 元素的实际宽度
        let visibleWidth = 0;

        // 判断元素在视口中的位置
        if (rect.left < 0 ) {
            if (rect.right < 0 ) {
                // 未进入
            }else {
                visibleWidth=rect.right;
            }
            // 元素部分进入视口，且左侧超出视口
            // visibleWidth = rect.right;
        } else{
            if (rect.left >= viewportWidth) {
                // 元素超过视口中
            }else {
                if (rect.right >= viewportWidth){
                    visibleWidth=viewportWidth-rect.left
                }else {
                    if (elementWidth<=viewportWidth){
                        visibleWidth=elementWidth
                    }else {

                    }
                }
            }
        }

        // 计算占比
        const visibilityRatio = visibleWidth / viewportWidth;
        if (visibilityRatio>visibilityRatioBuf){
            visibilityRatioBuf=visibilityRatio;
            maxEle=element;
        }
        console.log(`Element ${element.className} visibility ratio:${visibilityRatio.toFixed(2)}`);
    });
    return maxEle
}

//基于文字位置精确反色
// const bgImage = document.getElementById('background-image');
function getTextInverseColor(imageElement, textElement) {
    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d');

    canvas.width = imageElement.width;
    canvas.height = imageElement.height;
    ctx.drawImage(imageElement, 0, 0, canvas.width, canvas.height);

    // 获取文字位置
    const textRect = textElement.getBoundingClientRect();
    const imageRect = imageElement.getBoundingClientRect();

    // 计算文字在图片中的相对位置
    const x = textRect.left - imageRect.left;
    const y = textRect.top - imageRect.top;
    const width = textRect.width;
    const height = textRect.height;

    // 采样文字区域的颜色
    const imageData = ctx.getImageData(x, y, width, height);
    const data = imageData.data;

    let r = 0, g = 0, b = 0;
    const sampleSize = Math.min(1000, data.length / 4);
    const step = Math.floor(data.length / (sampleSize * 4));

    for (let i = 0; i < data.length; i += step * 4) {
        r += data[i];
        g += data[i + 1];
        b += data[i + 2];
    }

    const count = Math.floor(data.length / (step * 4));
    r = Math.floor(r / count);
    g = Math.floor(g / count);
    b = Math.floor(b / count);

    // 计算反色
    const inverseR = 255 - r;
    const inverseG = 255 - g;
    const inverseB = 255 - b;

    // 应用反色
    textElement.style.color = `rgb(${inverseR}, ${inverseG}, ${inverseB})`;
    // 可选：添加文字阴影增强可读性
    textElement.style.textShadow = `1px 1px 2px rgba(0,0,0,0.7)`;
}


function isDescendant(parent, child) {
    let node = child.parentNode;
    while (node !== null) {
        if (node === parent) return true;
        node = node.parentNode;
    }
    return false;
}

//选择页
function choosePage2(ele,f1,f2) {
    let div=document.createElement('div');
    div.setAttribute('class','sep')
    ele.appendChild(div)
    // div.addEventListener()
    let backGround = document.createElement('div')
    backGround.setAttribute('id','backgroundChooes')
    div.appendChild(backGround)

    backGround.addEventListener('click',function (e) {
        if (e.target!==this){
            return false
        }
        ele.removeChild(div)
        e.stopPropagation();
        return false;
    })

    let controlb = document.createElement('div')
    controlb.setAttribute('class','controlb')
    backGround.appendChild(controlb)

    let rig = document.createElement('div')
    rig.setAttribute('class','button')
    controlb.appendChild(rig)
    rig.addEventListener('click',function (e) {
        f1()
        if (e.target!==this){
            return false
        }
        ele.removeChild(div)
        e.stopPropagation();
        return false;
    })
    rig.textContent = '确定'


    if (f2!=null){
        let cls = document.createElement('div')
        cls.setAttribute('class','button')
        controlb.appendChild(cls)
        cls.addEventListener('click',function (e) {
            f2()
            if (e.target!==this){
                return false
            }
            ele.removeChild(div)
            e.stopPropagation();
            return false;
        })
        rig.textContent = '取消'
    }
}

// 阻止所有子元素的冒泡
function stopAll(ele) {
    ele.querySelectorAll('*').forEach(child => {
        child.addEventListener('click', (event) => {
            event.stopPropagation();
        });
    });
}

function createInputBord(node,text="") {
    let div=document.createElement('div');
    div.setAttribute('class','sep')
    node.appendChild(div)

    const backgroundCM = document.createElement('div');
    backgroundCM.id = 'backgroundCM';
    backgroundCM.addEventListener('click',function (e) {
        e.preventDefault()
        e.stopPropagation()
        if (e.target!== this){
            return true
        }
        let f= node.firstChild
        clearEleChildren(node)
        node.appendChild(f)
    },false)

// 创建setInput div
    const setInput = document.createElement('div');
    setInput.className = 'setInput';

    let con = document.createElement('div');
    con.className = 'con';
    con.id = 'InputBordText'
    con.textContent = text
// 组装backgroundCM
    setInput.appendChild(con)
    backgroundCM.appendChild(setInput);
    div.appendChild(backgroundCM)
    node.appendChild(div)

    return setInput
}

function createInput(setInput,kf,cf,placeholder="请输入",eleId = "") {
// 创建input元素
    const ipInput = document.createElement('input');
    ipInput.type = 'text';
    ipInput.id = eleId;
    ipInput.className = 'ipInput'
    ipInput.placeholder = placeholder;
    let kdf = function (e) {
        if(e.keyCode ===13){
            kf(e)
        }
    }
    ipInput.addEventListener('keydown',kdf)
    // ipInput.setAttribute('onkeydown', 'enterIp()');

// 创建containerIpInput div
    const containerIpInput = document.createElement('div');
    containerIpInput.className = 'containerInput';
    containerIpInput.addEventListener('click',cf)
    // containerIpInput.setAttribute('onclick', cf());

    let con = document.createElement('div');
    con.className = 'con';

// 创建内部元素
    const blueblockip = document.createElement('div');
    blueblockip.className = 'blueblockInput';

    const arc = document.createElement('div');
    arc.className = 'arc';

    const circle = document.createElement('div');
    circle.className = 'circle';

    const circlebeforeip = document.createElement('div');
    circlebeforeip.className = 'circlebeforeInput';

    const circleafterip = document.createElement('div');
    circleafterip.className = 'circleafterInput';

// 组装containerIpInput
    containerIpInput.appendChild(blueblockip);
    containerIpInput.appendChild(arc);
    containerIpInput.appendChild(circle);
    containerIpInput.appendChild(circlebeforeip);
    containerIpInput.appendChild(circleafterip);

// 组装setInput
    con.appendChild(ipInput)
    con.appendChild(containerIpInput);
    setInput.appendChild(con);
}

let backFun
// 解决 HBuilder X 打包的 APP 按返回键直接退出的问题
document.addEventListener('plusready', function() {
    var first = 0;
    var webview = window.plus.webview.currentWebview();

    // 监听物理返回键
    window.plus.key.addEventListener('backbutton', function() {
        webview.canBack(function(e) {
            if (e.canBack) {
                // 如果 webview 有历史记录，则返回上一页
                // webview.back();
                try {
                    backFun()
                }catch (e){
                    console.log(e)
                }

            } else {
                // 如果没有历史记录，提示“再按一次退出应用”
                if(first===0){
                    try {
                        backFun()
                    }catch (e){
                        console.log(e)
                    }
                    first===1;
                }
                if (first===1) {
                    first = new Date().getTime();
                    window.plus.nativeUI.toast('再按一次退出应用');
                    setTimeout(function() {
                        first = 0;
                    }, 1000);
                    first===2;
                } else {

                        // 一秒钟内按了两次，退出应用
                    plus.runtime.quit();

                }
            }
        });
    });
});