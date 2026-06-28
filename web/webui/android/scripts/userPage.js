console.log('userPageFun')
function userPageFun() {
    fetch("http://" + address + ":" + port + "/static/webui/htmls/userPage.html")
        .then(Response => Response.text())
        .then(text => {
            let div=document.createElement('div');
            div.setAttribute('class','sep')
            this.appendChild(div)
            // div.addEventListener()
            div.insertAdjacentHTML ('beforeend',text);
            const con = this;
            document.querySelector('#backgroundUserPage').addEventListener('click',function (e) {
                if (e.target!==this){
                    return false
                }
                con.removeChild(div)
                e.stopPropagation();
                return false;
            })
            document.querySelector('#setAutoLink').addEventListener('click',function (e) {
                if (e.target!==this){
                    return false
                }
                let username = con.querySelector('.tx').textContent
                fetch("http://" + address + ":" + port + "/map/Index/setAutoLink?"+username)
                    .then((Response) => Response.ok)
                    .then((json)=>{
                        outTimeNotic("添加成功",2000)
                    }).catch(e=>{ outTimeNotic("添加失败",2000)})
                e.stopPropagation();
                return false;
            })
            document.querySelector('#unsetAutoLink').addEventListener('click',function (e) {
                if (e.target!==this){
                    return false
                }
                let username = con.querySelector('.tx').textContent
                fetch("http://" + address + ":" + port + "/map/Index/unsetAutoLink?"+username)
                    .then((Response) => Response.ok)
                    .then((json)=>{
                        outTimeNotic("取消成功",2000)
                    })
                e.stopPropagation();
                return false;
            })
            // this.addEventListener('onkeydown', enterlis)
            console.log("aaaaa")
            this.removeEventListener('click',scanIp);
        })
    return false
}