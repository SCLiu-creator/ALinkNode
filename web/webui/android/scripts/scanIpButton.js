
document.querySelector('#scanIpbutton').addEventListener('click',scanIp)

function scanIp() {
    fetch("http://" + address + ":" + port + "/static/webui/htmls/findIp.html")
        .then(Response => Response.text())
        .then(text => {
            let div=document.createElement('div');
            div.setAttribute('class','sep')
            this.appendChild(div)
            // div.addEventListener()
            div.insertAdjacentHTML ('beforeend',text);
            document.querySelector('#backgroundIp').addEventListener('click',outClick)
            // this.addEventListener('onkeydown', enterlis)
            console.log("aaaaa")
            this.removeEventListener('click',scanIp);
        })
    return false
}

function enterIp() {
    if (window.event.keyCode==13){
        let node=document.querySelector('#IpInput');
        let sep=document.querySelector('.sep');

        fetch("http://"+address+":" + port + "/map/Index/scanIp?"+ node.value)
            .then((Response)=>Response.text()).
        then(text=>{
            sep.parentNode.addEventListener('click',scanIp);
            sep.parentNode.removeChild(sep);
        })
    }
}
function rightIp() {
    let node=document.querySelector('#IpInput');
    let sep=document.querySelector('.sep');

    fetch("http://"+address+":" + port + "/map/Index/scanIp?"+ node.value)
        .then((Response)=>{
            sep.parentNode.addEventListener('click',scanIp);
            sep.parentNode.removeChild(sep);

    })
}

function outClick(e) {
    if (e.target!==this){
        return false
    }
    let scanIpbutton=document.querySelector('#scanIpbutton')
    scanIpbutton.innerHTML="scanIp"
    e.stopPropagation();
    scanIpbutton.addEventListener('click',scanIp)
    return false;
}






