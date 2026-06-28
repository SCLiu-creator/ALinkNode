

function addChat() {
    let opt=document.querySelector('#chatsCon .optFile')
    const showfile = document.getElementById('showFile');

// 获取不包括内边距的宽度
    const widthWithoutPadding = showfile.offsetWidth -
        (parseFloat(getComputedStyle(showfile).paddingLeft) + parseFloat(getComputedStyle(showfile).paddingRight));

// 获取不包括内边距的高度
    const heightWithoutPadding = showfile.offsetHeight -
        (parseFloat(getComputedStyle(showfile).paddingTop) + parseFloat(getComputedStyle(showfile).paddingBottom));


    let b = document.createElement('div')
    b.setAttribute('class','bg')
    let bg = document.createElement('div')
    bg.setAttribute('class','background')
    bg.style.width=widthWithoutPadding+'px'
    bg.style.height=heightWithoutPadding+'px'
    opt.appendChild(b)
    b.appendChild(bg)
    bg.onclick = function (e) {
        if(e.target===this){
            clearEleChildrenClass(opt,'bg')
        }
    }

    let opb = document.createElement('div')
    opb.setAttribute('class','opb')

    let iName = document.createElement('input')
    iName.setAttribute('id','opbInput')
    iName.setAttribute('type','text')
    iName.setAttribute('placeholder','mc')
    iName.onkeydown=null

    let rig = document.createElement('div')
    rig.setAttribute('class','Input')
    rig.innerHTML= '      <div class="blueblockip"></div>\n' +
                    '      <div class="arc"></div>\n' +
                    '      <div class="circle"></div>\n' +
                    '      <div class="circlebeforeip"></div>\n' +
                    '      <div class="circleafterip"></div>\n'

    bg.appendChild(opb)
    opb.appendChild(iName)
    opb.appendChild(rig)
    outTimeNotic()
    rig.onclick = function (e) {
        fetch("http://" + address + ":" + port + "/map/chat/createChats?"+iName.value)
            .then(Response => Response.json())
            .then(json => {
                let chatCon=document.getElementById('chatsCon')
                let name=json['name']
                let num=json['num']
                let pic = json['pic']

                let chat=document.createElement('div')
                chat.setAttribute('class','chat')
                chat.textContent=name
                chat.dataset.num=num

                const lastChild = chatCon.lastChild;
                // 在最后一个子元素之前插入新元素
                chatCon.insertBefore(chat, lastChild);
                outTimeNotic("创建成功",2000)
            }).catch(()=>{
            outTimeNotic("创建失败",2000)
            }).finally(()=>{
                clearEleChildrenClass(opt,'bg')
            })
    }
}

async function getChats() {
    try {
        const response = await fetch(chatUrl, {
            method: 'post',
            body: JSON.stringify(chatpara)
        });
        const json = await response.json();
        console.log(json);

        // 如果第一次请求返回空对象，尝试备用 URL
        if (json === '{}' || Object.keys(json).length === 0) {
            const backupResponse = await fetch(`http://${address}:${port}/map/chat/getChats`, {
                method: 'post',
                body: JSON.stringify(chatpara)
            });
            const backupJson = await backupResponse.json();
            return backupJson; // 返回备用请求的结果
        }

        return json; // 返回第一次请求的结果
    } catch (error) {
        console.error("Error fetching chats:", error);
        throw error; // 抛出错误，让外部调用者处理
    }
}
