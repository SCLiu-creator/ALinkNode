let chatpara={
    'num':0
}
let chatUrl=`http://${address}:${port}/map/chat/context?`;

function reloadchat(username) {
    if(username==null){
        let h3 = document.querySelector('.chatcontainer h3')
        if(h3===null||h3===undefined){
            username=window.selectUserItem.dataset.username
        }else {
            username=document.querySelector('.chatcontainer h3').textContent;
        }
    }
    // 判断该元素（包含子元素）是否正在被 hover
    const isHovered = document.querySelector('.chatcontainer .box').matches(':hover');

    if (isHovered) {
        return
    } else {
        console.log('鼠标不在该元素上。');
    }
    chatpara['user']=username
    fetch(chatUrl,{
        method : 'post',
        body:JSON.stringify(chatpara)}).
    then((Response)=>Response.json()).
    then((json)=>{
        console.log(json);
        return json}).
    then((json)=>{
        let chatContextlist=document.querySelector('.chatcontainer .box');
        document.querySelector('.chatcontainer h3').textContent=username;
        // let jsons=JSON.parse(json)
        chatContextlist.innerHTML=""
        for(var user of json){
            createChatDiv(user,chatContextlist,username);
        }
        if(getVisibilityRatio(chatContextlist)>50){
            scrollToEle(chatContextlist,7)
        }
    })
}
(function (){
    let chatInput=document.getElementById('chatInput')
    // 绑定keydown事件
    chatInput.addEventListener('keydown', function(event) {
        // 检查是否按下了Shift键和Enter键
        if (event.shiftKey && event.key === 'Enter') {
            // 阻止默认的换行行为
            event.preventDefault();

            // 这里写上你想要执行的代码
            console.log('Shift + Enter was pressed!');
            chatupdate()
            // 你可以在这里执行任何操作，比如提交表单、触发其他函数等
        }
    });
})()
// reloadchat();
// window.username='';
function chatFileChange(e) {
    const label = document.querySelector('label[for="chatFile"]');
    const fileInput = document.querySelector('#chatFile')
    if (label && fileInput.files.length > 0) {
        // 直接设置 style 属性
        label.style.background = '#e0f7fa';
        label.style.border = '1px solid #4dd0e1';
        label.style.boxShadow = '0 0 8px rgba(77, 208, 225, 0.6)';
    } else {
        // 恢复默认样式
        label.style = null;
        // label.style.background = '#f0f0f0';
        // label.style.border = 'none';
        // label.style.boxShadow = 'none';
    }
}
document.querySelector('#chatFile').addEventListener('change', chatFileChange);

function createChatDiv(data,box,username){
    if (data===undefined||data===null){
        return ;
    }
    var listdiv=document.createElement('div');
    listdiv.setAttribute('class','list');
    if(data['u']===localUser){
        listdiv.style.backgroundImage = `url("${urlBackPic}")`
    }

    var columndiv=document.createElement('div');
    var contextdiv=document.createElement('div');
    var deldiv=document.createElement('div');
    columndiv.setAttribute('class','userColumn');
    if(data['file']!=null && data['file']!==''){
        let param={}
        param['user']=username
        param['file']=data['file']
        param=JSON.stringify(param)
        let img =document.createElement('img')
        img.setAttribute('class','chatFile')
        img.src=`http://${address}:${port}/map/chat/getData?${param}`
        columndiv.appendChild(img)
        enableLongPress(img,chatImgLong,chatImgClick)
    }

    contextdiv.setAttribute('class','chatC');
    deldiv.setAttribute('class','del');
    deldiv.innerText="x"
    let h1=document.createElement('h1');
    h1.setAttribute('class','sn');
    h1.dataset.sn=data['sn']
    h1.dataset.n = data['n']
    // var h2=document.createElement('h2');
    let p=document.createElement('p');
    h1.innerHTML='<small>#</small>'+data['date'];
    p.innerText=data['text'];
    if ((data['text']===undefined||data['text']===null) &&  (data['file']===undefined||data['file']===null) ){
        return ;
    }
    box.appendChild(listdiv);
    listdiv.appendChild(columndiv);
    listdiv.appendChild(contextdiv);

    contextdiv.appendChild(h1)
    // contextdiv.appendChild(h2)
    contextdiv.appendChild(p)
    contextdiv.appendChild(deldiv);
    deldiv.addEventListener('click',delchat);
    contextdiv.dataset.date=data['date'];
    contextdiv.dataset.file=data['file'];
    contextdiv.dataset.user=data['u'];
    contextdiv.dataset.num=data['n'];
    return listdiv;
}

function chatImgLong(e) {
    let path=e.target.dataset.path

    if (!path){
        let cc=e.target.parentNode.parentNode.querySelector('.chatC')
        path=cc.dataset.file
    }else {
        e.preventDefault()
        e.stopPropagation()
        e.stopImmediatePropagation()
    }

    let chatC=document.createElement('div')
    chatC.setAttribute('class','chatc')
    let showCO=document.createElement('div')
    showCO.setAttribute('class','chatcO')

    let showB=document.createElement('div')
    showB.setAttribute('class','chatcOB')
    showB.addEventListener('click',function (e) {
        chatC.parentNode.removeChild(chatC)
    })

    let but=document.createElement('div')
    but.setAttribute('class','listbutton')
    but.textContent="下载"
    but.dataset.path=path
    but.addEventListener("click",downFile)

    // let save=document.createElement('div')
    // save.setAttribute('class','listbutton')
    // save.textContent="保存"
    // save.dataset.path=path
    // save.addEventListener("click",saveFile)
    //
    let name = getBaseName(path)
    but.addEventListener('click', function() {
        const data = new Blob(['Hello, world!'], { type: 'text/plain' });
        const url = window.URL.createObjectURL(data);
        const a = document.createElement('a');
        a.style.display = 'none';
        a.href = url;
        a.download = name; // 设置下载的文件名
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
    });

    let save=document.createElement('div')
    save.setAttribute('class','listbutton')
    save.textContent="保存"
    save.dataset.path=path
    save.addEventListener("click",saveFile)


    let non =document.querySelector('.chatcontainer .non')
    non.innerHTML=''
    non.appendChild(chatC)
    chatC.appendChild(showB)
    chatC.appendChild(showCO)
    showCO.appendChild(but)
    showCO.appendChild(save)
}
function chatImgClick(e) {
    e.preventDefault()
    e.stopPropagation()
    // let chat =document.querySelector('.chatcontainer')
    let non =document.querySelector('.chatcontainer .non')
    non.innerHTML=''
    let view = document.createElement('div')
    view.setAttribute('class','view')
    let img = document.createElement('img')
    let oimg=e.target
    img.src=oimg.src

    let cc=e.target.parentNode.parentNode.querySelector('.chatC')
    let path=cc.dataset.file
    img.dataset.path = path

    enableLongPress(img,chatImgLong,function (e) {})
    non.appendChild(view)
    view.appendChild(img)
    let f=(e)=>{
        e.preventDefault()
        e.stopPropagation()
        view.removeEventListener('click',f)
        non.innerHTML=''
    }
    view.addEventListener('click',f)
}
function chatupdate(e){
    // if (e){
    //     e.stopPropagation()
    // }else {
    //     return;
    // }
    console.log('chatupdate')
    let node=document.querySelector('#chatInput');

    let text=node.value
    let fileInput = document.getElementById('chatFile');
    if ((node.value==="" || node.value===undefined) && !fileInput.files[0]){
        return false;
    }
    let rebody={};
    let user = document.querySelector(".chatcontainer h3").textContent
    rebody['user']=window.selectUserItem.dataset.username
    rebody['user']=user
    rebody['textlen']=text.length
    rebody['num']=chatpara['num']
    let res;
    if (text.length>1000){
        res=fetch("http://"+address+":" + port + "/map/chat/postData?"+JSON.stringify(rebody),{
            method : 'post',
            body:text
        })
        rebody['textlen']=0
        if(fileInput.files && fileInput.files[0]){
            let file = fileInput.files[0];
            console.log('文件名:', file.name);
            console.log('文件类型:', file.type);
            console.log('文件大小:', file.size, '字节');
            rebody['file']=file.name
            rebody['filesize']=file.size
            rebody['text']='文件'

            res=fetch("http://"+address+":" + port + "/map/chat/postData?"+JSON.stringify(rebody), {
                method: 'POST',
                body: file, // 文件作为请求体发送
                headers: {
                    'Content-Type': 'multipart/form-data', // 设置正确的 Content-Type
                    // 'Content-Disposition': 'form-data', // 设置表单数据
                },
            }).then(response => response.text())
        }
    }else {
        if (text===''||text===undefined||text===null){
            rebody['text']='文件'
        }else {
            rebody['text']=text
        }

        if (fileInput.files && fileInput.files[0]) {
            let file = fileInput.files[0];
            console.log('文件名:', file.name);
            console.log('文件类型:', file.type);
            console.log('文件大小:', file.size, '字节');
            rebody['file']=file.name
            rebody['filesize']=file.size
            // 假设这里使用FormData和fetch API上传文件
            // 假设 file 是用户选择的文件
            // const reader = new FileReader();
            // reader.readAsText(file); // 读取文件为文本
            // reader.readAsDataURL(file); // 读取文件为 Base64 编码
            // reader.readAsArrayBuffer(file); // 读取文件为 ArrayBuffer
            // let formData = new FormData();
            // formData.append('file', file);
            res=fetch("http://"+address+":" + port + "/map/chat/postData?"+JSON.stringify(rebody), {
                method: 'POST',
                body: file, // 文件作为请求体发送
                headers: {
                    'Content-Type': 'multipart/form-data', // 设置正确的 Content-Type
                    // 'Content-Disposition': 'form-data', // 设置表单数据
                },
            }).then(response => response.text())
        } else {
            res=fetch("http://"+address+":" + port + "/map/chat/postData?"+JSON.stringify(rebody))
                .then(response => response.text())
        }
    }
    res.then(text=>{
        fileInput.value=''

        chatFileChange()

        if (text==='false'|| text ===''){
            showText1('失败')
        }else {
            showText1('成功')
            reloadchat()
            node.value=''
        }
    }).catch(error => console.error('上传失败:', error));
    return false;
}




let delchatdata=`http://${address}:${port}/map/chat/delData?`;
function delchat(e) {
    let node=e.target.parentNode;
    let chatpara=getChatPara(e.target)
    let username = document.querySelector('.chatcontainer h3').textContent;
    fetch(delchatdata+username,{
        method : 'post',
        body:JSON.stringify(chatpara)}).
    then((Response)=>Response.json()).
    then((json)=>{
        node.parentNode.parentNode.removeChild(node.parentNode)
        })
}

function getChatPara(element) {
    // 获取所有的.my-element元素
    let elements = element.parentNode.childNodes;

    // 用于存储找到的<a>元素的数组
    let chatpara={}

    let sn=element.parentNode.querySelector('.sn')

    // 遍历每个.my-element元素
    elements.forEach(child => {
        // 使用querySelectorAll查找该元素下的所有<a>子元素
        // if (child.classList.contains('')){
        //     aChildren = child.querySelectorAll('a');
        // }
        if (child.nodeType === 1 && child.tagName.toLowerCase() === 'p'){
            chatpara['text']=child.textContent
        }
        // 将找到的<a>元素添加到aElements数组中
        // aElements.push(...aChildren);
    });

    let file=element.parentNode.dataset.file
    if(!(file==='undefined' ||
        file===undefined ||
        file===null ||file==="null" ||
        file==="")){
        chatpara['file'] = file
    }
    chatpara['sn']=sn.dataset.sn
    chatpara['date']=element.parentNode.dataset.date
    chatpara['user']=element.parentNode.dataset.user;
    let num  = element.parentNode.dataset.num
    if (num===undefined ||num ==null){
        num=0
    }
    chatpara['num']=Number(num);
    // 现在aElements数组包含了所有的<a>子元素
    return chatpara;
}
