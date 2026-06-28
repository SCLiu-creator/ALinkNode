// var iframe = document.getElementById('myIframe');
// var iframeWindow = iframe.contentWindow;
// iframeWindow.postMessage('Hello, iframe!', 'https://example.com');
// window.addEventListener('message', function(event) {
//     // 检查消息来源，确保它来自你信任的源
//     if (event.origin !== 'https://your-parent-page.com') return;
//
//     console.log('Received message:', event.data); // 输出 'Hello, iframe!'
// }, false);
//
// document.domain = 'example.com'; // 设置相同的父域
console.log('loadproxy')
inp=document.querySelector(".input");

// inp.addEventListener('click',inputProxyUrl)
// async function inputProxyUrl() {
//     let doc=document.querySelector('.input')
//     await fetch("http://" + window.address + ":" + window.port + "/static/webui/htmls/inputBoxProxy.html")
//         .then(Response=>Response.text())
//         .then(text=>{
//             // this.innerHTML=text;
//             this.addEventListener('onkeydown',tourl)
//             console.log("aaaaa")
//         })
//     // this.innerHTML
//     // ('#input').load('htmls/inputBox.html');
// }

const iframe = document.getElementById('myIframe');
function tourl() {
    let node=document.querySelector('.proxyContainer #urlInput');
    let rebody={};

    rebody['user']=''
    rebody['name']=node.value
    let iframeWindow = iframe.contentWindow;
    iframeWindow.postMessage('Hello, iframe!', node.value)
}
function enterproxy() {
    let node=document.querySelector('.proxyContainer #urlInput');
    let rebody={};

    rebody['name']=node.value
    let iframeWindow = iframe.contentWindow;
    // iframeWindow.postMessage('Hello, iframe!', node.value)
    iframe.setAttribute('src',node.value)
}
