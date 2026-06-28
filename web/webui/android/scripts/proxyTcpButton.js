
document.querySelector('#ProxyTcpButton').addEventListener('click',ProxyTcpFun)
console.log("bind ProxyTcpFun")
function ProxyTcpFun() {
    console.log("bind ProxyTcpFun")
    let proxyTcpBut = document.querySelector('#ProxyTcpButton')
    let border = createInputBord(proxyTcpBut,window.selectUserItem.dataset.username)
    let mon = function (e) {
        let data = {}
        let monFn = border.querySelector('#PTS')
        let monFz = border.querySelector('#PTP')
        data['user'] = border.querySelector('#InputBordText').textContent
        data['sp'] = monFn.value
        data['cp'] = monFz.value
        let jsondatas = JSON.stringify(data)
        fetch("http://" + address + ":" + port + "/map/Index/proxyTcp", {
            method: 'post',
            body: jsondatas,
        }).then(response=>response.ok).then((ok)=>{
            if(ok){
                showText1("成功")
            }
        }).catch(e=>{showText1("失败")})
    }
    createInput(border,mon,mon,'服务端口',"PTS")
    createInput(border,mon,mon,'目标端口',"PTP")

    return false
}









