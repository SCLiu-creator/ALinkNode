var item = document.querySelector('.viewArea .item');
var contextContainer = document.querySelector('.viewArea').parentNode.parentNode;

let port = window.location.port
let address = window.location.hostname
console.log('item'+item);
let start = 0; // 开始位置
var pageSize = 20; // 每页展示的数据
var total = 300; //数据总长度

// var itemHeight = 61; // 每个item的高度
var itemStyle = getComputedStyle(item);
var itemHeight = Number(itemStyle.height.split('px')[0]) +
    Number(itemStyle.borderTopWidth.split('px')[0]) +
    Number(itemStyle.borderBottomWidth.split('px')[0]); // 每个item的高度

window.addEventListener('load', () => {
    itemHeight = item.getBoundingClientRect().height; // 每个item的高度
    console.log('itemHeight', itemHeight);
    window.removeEventListener('load',this)
});

const resizeObserver = new ResizeObserver(entries => {
    for (let entry of entries) {
        // 当元素尺寸确定后，这里会被调用
        itemHeight = item.getBoundingClientRect().height; // 每个item的高度
        console.log('itemHeight', itemHeight)
        // resizeObserver.unobserve(entry.target);
        // 彻底断开所有观察连接，销毁观察者
        resizeObserver.disconnect();
    }
});

// 开始观察该元素
resizeObserver.observe(item);



// 设置数据列表的总高度
document.querySelector('#content-l').style.height = itemHeight * total + 'px';
// updateDom(start, pageSize, itemHeight, 0);
async function updateDom(start, pageSize, height) {
    // document.querySelector('.viewArea').style.transform = `translateY(${ height-itemHeight}px)`;
    document.querySelector('.viewArea').style.transform = `translateY(${ height}px)`;
    // document.querySelector('.viewArea').scrollTo(0,height)
    try{
        const response = await fetch("http://"+address+":" +port+"/map/Index/Userlist?"+start, {
            method:'get',
            withCredentials: true })
        const userlist = await response.json();

        // 5. 验证数据
        if (!userlist) {
            console.warn("Received empty userlist");
            // 可以选择是否继续调用 reloaditem，视业务逻辑而定
        }

        // 6. 渲染数据
        if (typeof reloaditem === 'function') {
            reloaditem(userlist, start, pageSize);
        } else {
            console.error("Function reloaditem is not defined");
        }
    }catch (error) {
        // 7. 统一捕获所有错误 (网络错误、JSON 解析错误、逻辑错误)
        console.error("Failed to update DOM:", error);
        // 可选：在这里显示一个友好的错误提示给用户，或者重试
        // alert("加载失败，请检查网络连接");
    }
}
// async function updateDom(start, pageSize, itemHeight) {
//     // 1. 构建 URL
//     let portStr = port ? `:${port}` : '';
//     const url = `http://${address}${portStr}/map/Index/Userlist?start=`+start;
//
//     try {
//         const response = await fetch(url, {
//             method: 'get',
//             credentials: 'include'
//         });
//
//         if (!response.ok) throw new Error('Network error');
//
//         const userlist = await response.json();
//
//         // 2. 数据回来后，静默更新 DOM 内容
//         // 此时列表已经滚到了正确的位置，我们只需要把里面的文字换掉
//         if (userlist && typeof reloaditem === 'function') {
//             reloaditem(userlist, start, pageSize);
//         }
//
//     } catch (error) {
//         console.error("Background update failed:", error);
//         // 错误时不做任何视觉处理，用户继续看旧数据即可
//     }
// }
//
//
// function reloaditem(userlist, start, pageSize) {
//     let allItems = document.querySelectorAll('.viewArea .item');
//     let leng = userlist ? userlist.length : 0;
//
//     // // 遍历可视区域内的 DOM 节点 (假设是 pageSize 个)
//     // for (let itemIndex = 0; itemIndex < pageSize; itemIndex++) {
//     //     // 确保 DOM 节点存在
//     //     if (!allItems[itemIndex]) break;
//     //
//     //     let dataIndex = start + itemIndex; // 当前 DOM 对应的数据索引
//     //
//     //     // 边界检查：如果超出数据总量
//     //     if (dataIndex >= total || dataIndex >= leng) {
//     //         // allItems[itemIndex].innerHTML = ''; // 清空
//     //         // allItems[itemIndex].style.visibility = 'hidden';
//     //         continue;
//     //     }
//     //
//     //     // 获取数据
//     //     const data = userlist[dataIndex];
//     //     const sn = data["nickName"] || '';
//     //     const su = data["username"] || '';
//     //     const pp = (data["address"] || '') + ':' + (data['port'] || '');
//     //
//     //     // 直接更新 HTML
//     //     // 注意：使用 innerHTML 会覆盖之前的内容，所以之前绑定的 onclick 会丢失
//     //     // 但因为我们是复用 DOM，所以需要在更新后重新绑定，或者使用事件委托
//     //     allItems[itemIndex].innerHTML =
//     //         `<div class="item0">${sn}</div>` +
//     //         `<div class="itemtext">${su}</div>` +
//     //         `<div class="pp">${pp}</div>`;
//     //
//     //     // 重新绑定点击事件 (因为 innerHTML 会清除旧的事件监听)
//     //     // 这里使用 onclick 属性赋值，比 addEventListener 更适合这种高频复用的场景
//     //     allItems[itemIndex].onclick = function(e) {
//     //         // 确保这两个函数在全局存在
//     //         if(typeof selectUserInlist === 'function') selectUserInlist.call(this, e);
//     //         if(typeof displayUCO === 'function') displayUCO.call(this, e);
//     //     };
//     //
//     //     allItems[itemIndex].style.visibility = 'visible';
//
//
//     console.log(`更新列表：共 ${allItems.length} 个DOM节点，需渲染 ${Math.min(pageSize, leng)} 条数据`);
//     for (let i = 0; i < leng; i++) {
//
//         // 2. 找到对应的 DOM 坑位
//         // 如果数据量超过了预置的 DOM 节点数，就停止（防止报错）
//         // 比如：HTML 只有 20 个 .item，但接口返回了 25 条，那只显示前 20 条
//         if (i >= allItems.length) {
//             console.warn(`DOM 节点不足：数据有 ${leng} 条，但页面只有 ${allItems.length} 个 .item 坑位`);
//             break;
//         }
//
//         let domNode = allItems[i];
//         const data = userlist[i];
//
//         const sn = data["nickName"] || '';
//         const su = data["username"] || '';
//         const pp = (data["address"] || '') + ':' + (data['port'] || '');
//
//         // 3. 查找子元素 (为了复用 DOM，避免 innerHTML 重置位置)
//         let item0 = domNode.querySelector('.item0');
//         let itemtext = domNode.querySelector('.itemtext');
//         let ppDiv = domNode.querySelector('.pp');
//
//
//         // 4. 如果是第一次，初始化结构
//         if (!item0) {
//             domNode.innerHTML =
//                 '<div class="item0"></div>' +
//                 '<div class="itemtext"></div>' +
//                 '<div class="pp"></div>';
//
//             // 重新获取引用
//             item0 = domNode.querySelector('.item0');
//             itemtext = domNode.querySelector('.itemtext');
//             ppDiv = domNode.querySelector('.pp');
//
//             // 绑定事件
//             domNode.onclick = function(e) {
//                 if(typeof selectUserInlist === 'function') selectUserInlist.call(this, e);
//                 if(typeof displayUCO === 'function') displayUCO.call(this, e);
//             };
//         }
//
//         if (!item0){
//             item0=document.createElement('div')
//             item0.setAttribute('class','item0')
//             domNode.appendChild(item0)
//         }
//         if (!itemtext){
//             itemtext=document.createElement('div')
//             itemtext.setAttribute('class','itemtext')
//             domNode.appendChild(itemtext)
//         }
//         if (!ppDiv){
//             ppDiv=document.createElement('div')
//             ppDiv.setAttribute('class','pp')
//             domNode.appendChild(ppDiv)
//         }
//
//         // 5. 更新内容
//         item0.textContent = sn;
//         itemtext.textContent = su;
//         ppDiv.textContent = pp;
//
//         // 确保显示
//         domNode.style.visibility = 'visible';
//     }
// }
//
// let isScrolling = false;
// let lastStart = -1;
// function handleScroller() {
//     // 使用 requestAnimationFrame 保证滚动流畅，不阻塞主线程
//     window.requestAnimationFrame(() => {
//         const currentScrollTop = container.scrollTop;
//
//         // 1. 计算当前应该显示的数据起始索引
//         const start = Math.floor(currentScrollTop / itemHeight);
//
//         // 2. 立即更新视觉位置 (不管数据有没有加载出来，先滚过去，显示旧的)
//         // 注意：这里我们不再依赖 updateDom 来移动位置，而是直接在这里移动
//         var fixedScrollTop = currentScrollTop - currentScrollTop % itemHeight;
//         const viewArea = document.querySelector('.viewArea');
//         // 使用 start * itemHeight 可以让列表对齐到每一项的顶部，避免滚到一半
//         // viewArea.style.transform = `translateY(${currentScrollTop}px)`;
//
//         // 3. 只有当起始索引发生变化时，才去请求新数据
//         if (lastStart !== start) {
//             if(start===0){
//                 lastStart = start;
//                 // 触发后台更新，但不阻塞滚动
//                 updateDom(start, pageSize, itemHeight);
//             }else {
//                 if(Math.abs(lastStart-start)>3){
//                     lastStart = start;
//                     // 触发后台更新，但不阻塞滚动
//                     updateDom(start, pageSize, itemHeight);
//                 }
//             }
//         }
//
//         isScrolling = false;
//     });
//     isScrolling = true;
// }
//
// let cont_l=document.querySelector('.viewArea').parentNode.parentNode;
// // 移除复杂的 throttle 封装，直接绑定
// cont_l.addEventListener('scroll', handleScroller, false);
// cont_l.addEventListener('scroll', unDisplayUCO);


function reloaditem(userlist,start, pageSize) {
    let all = document.querySelectorAll(' .viewArea .item');
    let leng = userlist.length;
    // var iterates=Object.entries(userlist)[Symbol.iterator]();
    let su;
    let sn;
    let pp;
    for (var i = start, itemIndex = 0+1, len = start + pageSize-1; i < len; i++, itemIndex++) {
        var index = i % pageSize;
        if (leng > i) {
            // st=JSON.stringify(userlist[i])
            st = userlist[i]
            console.log(userlist[i].constructor.name);
            su = st["username"]
            sn = st["nickName"]
            pp = st["address"]+':'+st['port']
            // console.log(s);
            all[itemIndex].innerHTML =
                '<div class="item0">' + sn + '</div>' +
                '<div class="itemtext">' + su + '</div>'+
                '<div class="pp">' + pp + '</div>'

            all[itemIndex].onclick = selectUserInlist
            all[itemIndex].addEventListener('click',displayUCO)
            all[itemIndex].querySelector('.pp').onclick= async function (e) {
                let ele= e.target
                let u=ele.parentNode.querySelector('.itemtext').textContent
                await fetch("http://"+address+":" +port+"/map/Index/showUserIp?"+u, {
                    method:'get',
                    withCredentials: true })
                    .then((Response) => Response.json())
                    .then((json)=>{
                        pp = json["address"]+':'+json['port']+' | '+json["inaddress"]+':'+json['inport']
                        ele.textContent=pp
                        setTimeout(function () {
                            ele.textContent = json["address"]+':'+json['port']
                        },8*1000)
                    })

            }
        } else {
            // all[itemIndex].innerHTML = '<div class="item0">' + sn + '</div><div class="itemtext">' + su + '</div>'
            all[itemIndex].innerHTML = i
        }
        // JSON.stringify(iterates.next());
    }
}
function smoothScrollTo(container, targetScrollTop, duration = 500) {
    const startScrollTop = container.scrollTop;
    const distanceToScroll = targetScrollTop - startScrollTop;
    let startTime = null;

    // 缓动函数：easeOutCubic，让动画结束时减速，更自然
    const easeOutCubic = (t) => {
        return 1 - Math.pow(1 - t, 3);
    };

    const animateScroll = (currentTime) => {
        if (startTime === null) startTime = currentTime;
        const timeElapsed = currentTime - startTime;

        // 计算动画进度 (0 到 1)
        const progress = Math.min(timeElapsed / duration, 1);

        // 应用缓动函数，并计算当前应该滚动到的位置
        const easeProgress = easeOutCubic(progress);
        container.scrollTop = startScrollTop + (distanceToScroll * easeProgress);

        // 如果动画未完成，则请求下一帧
        if (timeElapsed < duration) {
            requestAnimationFrame(animateScroll);
        }
    };

    // 启动动画
    requestAnimationFrame(animateScroll);
}

// 滚动处理函数
let recordeVaScrollTop=null
function handleScroller() {

    var lastStart = 0; // 上次开始的位置
    return () => {
        let cl = document.querySelector('#content-l')
        if(cl.style.display==='none'){
            // if(recordeVaScrollTop===null){
            //     recordeVaScrollTop = contextContainer.scrollTop-itemHeight;
            // }
            return false
        }

        var currentScrollTop = contextContainer.scrollTop-itemHeight;
        // if(currentScrollTop<0)currentScrollTop=0
        var fixedScrollTop = currentScrollTop - currentScrollTop % itemHeight;
        var start = Math.floor(currentScrollTop / itemHeight);
        if(start<0){
            // console.log("handleScroller");
            if (lastStart !== start) {
                lastStart = 0;
                updateDom(0, pageSize, 0);
                smoothScrollTo(contextContainer,itemHeight,630)
                // container.scrollTop = itemHeight
            }
        }else {
            // console.log("handleScroller");
            if (lastStart !== start) {
                lastStart = start;
                updateDom(start, pageSize, currentScrollTop);
                smoothScrollTo(contextContainer,currentScrollTop+itemHeight,630)
                // container.scrollTop = currentScrollTop+itemHeight
            }
        }

        // else {
        //     updateDom(start, pageSize, itemHeight, fixedScrollTop);
        // }
    }
}
// 防抖和节流
function throttle(fn, delay0, atleast) {
    let timer = null;
    let rAFtimer = null;
    let previous = 0;
    let delay= 0;

    return function (e) {
        // console.log(e.target)
        let now = Date.now();
        // if (now - previous > atleast) {
        //     // console.log('now - previous > atleast');
        //     fn();
        //     previous = now;
        // } else {
            delay=now -delay
            // console.log(delay);
            if (delay > 200) {
                // console.log('delay > 200');
                clearTimeout(timer);
                timer = setTimeout(function () {
                    fn();
                    previous = 0;
                }, 200);
            } else {
                // console.log('delay < 200');
                return
                // cancelAnimationFrame(rAFtimer);
                // rAFtimer = requestAnimationFrame(function () {
                //     window.requestIdleCallback(fn);
                // });
            }
        // }
        delay=Date.now()
    }
}
let cont_l=document.querySelector('.viewArea').parentNode.parentNode;
cont_l.addEventListener('scroll', throttle(handleScroller(), 200, 3000), false);
cont_l.addEventListener('scroll',unDisplayUCO)
// document.querySelector('.container').addEventListener('scroll', handleScroller(), false);



async function searchupdate( ) {
    let data=document.querySelector('#paraInput')
    let userlist
    if (showContentState){
        await fetch("http://"+address+":" +port+"/map/Index/secrchUserlist?"+data.value, {
            method:'get',
            withCredentials: true })
            .then((Response) => Response.json())
            .then((json)=>{userlist=json;})
        reloaditem(userlist,start,pageSize)
    }else {
        fetch("http://" + address + ":" + port + "/map/Index/secrchBindlist?"+data.value )
            .then((Response) => Response.json())
            .then((json)=>{
                reloadBindList(json)
            })
    }
}

function selectUserChat(useName) {
    fetch("http://" + address + ":" + port + "/map/chat/chats?"+useName)
        .then(Response => Response.json())
        .then(jsons => {
            let showFile = document.querySelector('#showFile')
            clearEleChildren(showFile)
            let chatCon=document.createElement('div')
            chatCon.setAttribute('id','chatsCon')
            let chatOp=document.createElement('div')
            chatOp.setAttribute('class','optFile')
            showFile.appendChild(chatCon)
            chatCon.appendChild(chatOp)

            let chat0=document.createElement('div')
            chat0.setAttribute('class','chat0')
            chatCon.appendChild(chat0)
            chat0.style.backgroundImage = `url("${urlBackPic}")`

            chat0.onclick=function (){
                loadChatHtml()
                chatpara['num'] = 0
                chatpara['user'] = useName
                chatpara['name'] = ""
                reloadchat(useName)
                getChat()
            }

            let fun = async function () {
                loadChatHtml()
                let n= this.dataset.num
                chatpara['num'] = Number(n)
                chatpara['user'] = useName
                chatpara['name'] = this.textContent
                await getChats()
                reloadchat(useName)
                getChat()
            }
            for (let data of jsons) {
                let name=data['name']
                let num=data['num']
                let pic = data['pic']

                let chat=document.createElement('div')
                chat.setAttribute('class','chat')
                chat.textContent=name
                chat.dataset.num=num
                chat.pic = pic
                chatCon.appendChild(chat)
                chat.onclick=fun
            }


            let chat=document.createElement('div')
            chatCon.appendChild(chat)
            let addbtn=document.createElement('addbtn')
            addbtn.setAttribute('class','addBtn')
            chat.appendChild(addbtn)
            // chat.innerHTML = '<addbtn class="addBtn"></addbtn>'
            addbtn.addEventListener('click',addChat)
        })
}

function getSelfChat(useName) {
    fetch("http://" + address + ":" + port + "/map/chat/getSelfChats")
        .then(Response => Response.json())
        .then(jsons => {
            let showFile = document.querySelector('#showFile')
            clearEleChildren(showFile)
            let chatCon=document.createElement('div')
            chatCon.setAttribute('id','chatsCon')
            let chatOp=document.createElement('div')
            chatOp.setAttribute('class','optFile')
            showFile.appendChild(chatCon)
            chatCon.appendChild(chatOp)

            let chat0=document.createElement('div')
            chat0.setAttribute('class','chat0')
            chatCon.appendChild(chat0)
            chat0.style.backgroundImage = `url("${urlBackPic}")`

            chat0.onclick=function (){
                loadChatHtml()
                chatpara['num'] = 0
                chatpara['user'] = null
                chatpara['name'] = null
                reloadchat(useName)
                getChat()
            }

            let fun = async function () {
                loadChatHtml()
                let n= this.dataset.num
                chatpara['num'] = Number(n)
                chatpara['user'] = useName
                chatpara['name'] = this.textContent
                await getChats()
                reloadchat(useName)
                getChat()
            }
            for (let data of jsons) {
                let name=data['name']
                let num=data['num']
                let pic = data['pic']

                let chat=document.createElement('div')
                chat.setAttribute('class','chat')
                chat.textContent=name
                chat.dataset.num=num
                chat.onclick=fun
                chatCon.appendChild(chat)
            }

            let chat=document.createElement('div')
            chatCon.appendChild(chat)
            let addbtn=document.createElement('addbtn')
            addbtn.setAttribute('class','addBtn')
            chat.appendChild(addbtn)
            // chat.innerHTML = '<addbtn class="addBtn"></addbtn>'
            addbtn.addEventListener('click',addChat)
        })
}

function selectUserInlist(e) {
    e.stopPropagation()
    e.preventDefault()
    let useName;
    let nickName;
    let b = true;
    let ele = e.target
    if (ele.classList.contains("item")) {
        nickName = ele.querySelector('.item0').textContent
        useName = ele.querySelector('.itemtext').textContent
    }else{
        return false
    }

    // if (useName===null){
    //     menuToggleHide()
    //     return false
    // }
    let b1=document.getElementById('bindUser')
    b1.style.backgroundColor=null
    b1.removeEventListener('click',removeUserBind)
    b1.removeEventListener('click',bingUser)
    b1.addEventListener('click',bingUser)
    let b2=document.getElementById('inbindUser')
    b2.style.backgroundColor=null
    b2.removeEventListener('click',removeUserBind)
    b1.removeEventListener('click',inbingUser)
    b1.addEventListener('click',inbingUser)
    let b3=document.getElementById('returnbindUser')
    b3.removeEventListener('click',removeUserBind)
    b1.removeEventListener('click',returnbingUser)
    b1.addEventListener('click',returnbingUser)
    b3.style.backgroundColor=null
        fetch("http://" + address + ":" + port + "/map/Index/secrchUserBind?" + useName)
            .then(Response => Response.json())
            .then(text => {
                if (text['e']==='true'){
                    if(text['sort']>0){
                        if(text['sort']===1){
                            b1.style.backgroundColor='rgb(170 112 221 / 90%)'
                            b1.addEventListener('click',removeUserBind)
                            b1.removeEventListener('click',bingUser)
                        }else if(text['sort']===2){
                            b2.style.backgroundColor='rgb(170 112 221 / 90%)'
                            b2.addEventListener('click',removeUserBind)
                            b2.removeEventListener('click',inbingUser)
                        } else if(text['sort']===3){
                            b3.style.backgroundColor='rgb(170 112 221 / 90%)'
                            b3.addEventListener('click',removeUserBind)
                            b3.removeEventListener('click',returnbingUser)
                        }
                    }else {
                        if(text['sort']===-1){
                            b1.style.backgroundColor='rgb(255 183 206 / 80%)'
                            b1.addEventListener('click',removeUserBind)
                            b1.removeEventListener('click',bingUser)
                        }else if(text['sort']===-2){
                            b2.style.backgroundColor='rgb(255 183 206 / 80%)'
                            b2.addEventListener('click',removeUserBind)
                            b2.removeEventListener('click',inbingUser)
                        } else if(text['sort']===-3){
                            b3.style.backgroundColor='rgb(255 183 206 / 80%)'
                            b3.addEventListener('click',removeUserBind)
                            b3.removeEventListener('click',returnbingUser)
                        }
                    }

                }else {
                    b1.style.backgroundColor=null
                    b2.style.backgroundColor=null
                    b3.style.backgroundColor=null
                    b1.addEventListener('click',bingUser)
                    b1.addEventListener('click',inbingUser)
                    b1.addEventListener('click',returnbingUser)
                }
            })
        if (window.localUserName !== useName) {
            let docs = document.querySelectorAll('.Menulist li');
            for (let d in docs) {
                try {
                    if (docs[d].getAttribute('username') === useName) {
                        b = false;
                    }
                } catch (e) {
                }
            }
        }
        if (b) {
            addDragBind(false, useName, nickName)
        }


        // menuToggleShow()

        this.dataset.username = useName;
        if (window.selectUserItem === null || typeof (window.selectUserItem) === "undefined") {
            window.selectUserItem = this;
            // this.style.color = '#c9d7e7';
            this.style.background = 'linear-gradient(rgba(212 235 254 0.9), rgba(163 220 255 0.89))';
        } else {
            // window.selectUserItem.style.color = '#7f9ec4'
            // window.selectUserItem.style.background = 'linear-gradient(rgb(220, 232, 246), rgb(181 215 236))';
            window.selectUserItem.style.background = null;
            // this.style.color = '#c9d7e7';
            this.style.background = 'linear-gradient(rgb(212 235 254), rgb(239 249 255))';
            window.selectUserItem = this;
        }

    // fetch("http://" + address + ":" + port + "/map/Index/secrchUserBind?" + useName)
    //     .then(Response => Response.json())
    //     .then(text => {
    //         if(! e.target.classList.contains('item0')){
    //             return
    //         }
    //         if (text['e']==='true'){
    //             selectUserChat(useName)
    //         }else {
    //             let showFile = document.querySelector('#showFile')
    //             clearEleChildren(showFile)
    //         }
    //         if(useName===localUser){
    //             getSelfChat(useName)
    //         }
    //     })
}


function selectUserBindlist(e) {
    e.stopPropagation()
    e.preventDefault()
    let useName;
    let nickName;
    let b = true;
    let ele = this
     if (ele.classList.contains("binduser")) {
        nickName = ele.querySelector('.txn').textContent
        useName = ele.querySelector('.tx').textContent
    }

    // if (useName===null){
    //     menuToggleHide()
    //     return false
    // }
    let b1=document.getElementById('bindUser')
    b1.style.backgroundColor=null
    b1.removeEventListener('click',removeUserBind)
    b1.removeEventListener('click',bingUser)
    b1.addEventListener('click',bingUser)
    let b2=document.getElementById('inbindUser')
    b2.style.backgroundColor=null
    b2.removeEventListener('click',removeUserBind)
    b1.removeEventListener('click',inbingUser)
    b1.addEventListener('click',inbingUser)
    let b3=document.getElementById('returnbindUser')
    b3.removeEventListener('click',removeUserBind)
    b1.removeEventListener('click',returnbingUser)
    b1.addEventListener('click',returnbingUser)
    b3.style.backgroundColor=null
    fetch("http://" + address + ":" + port + "/map/Index/secrchUserBind?" + useName)
        .then(Response => Response.json())
        .then(text => {
            if (text['e']==='true'){
                if(text['sort']===1){
                    b1.style.backgroundColor='rgb(170 112 221 / 90%)'
                    b1.addEventListener('click',removeUserBind)
                    b1.removeEventListener('click',bingUser)
                }else if(text['sort']===2){
                    b2.style.backgroundColor='rgb(170 112 221 / 90%)'
                    b2.addEventListener('click',removeUserBind)
                    b1.removeEventListener('click',inbingUser)
                } else if(text['sort']===3){
                    b3.style.backgroundColor='rgb(170 112 221 / 90%)'
                    b3.addEventListener('click',removeUserBind)
                    b1.removeEventListener('click',returnbingUser)
                }
            }else {
                b1.style.backgroundColor=null
                b2.style.backgroundColor=null
                b3.style.backgroundColor=null
                b1.addEventListener('click',bingUser)
                b1.addEventListener('click',inbingUser)
                b1.addEventListener('click',returnbingUser)
            }
        })
    if (window.localUserName !== useName) {
        let docs = document.querySelectorAll('.Menulist li');
        for (let d in docs) {
            try {
                if (docs[d].getAttribute('username') === useName) {
                    b = false;
                }
            } catch (e) {
            }
        }
    }
    if (b) {
        addDragBind(false, useName, nickName)
    }


    // menuToggleShow()

    this.dataset.username = useName;
    if (window.selectUserItem === null || typeof (window.selectUserItem) === "undefined") {
        window.selectUserItem = this;
        // this.style.color = '#c9d7e7';
        this.style.background = 'linear-gradient(rgba(212 235 254 0.9), rgba(163 220 255 0.89))';
    } else {
        // window.selectUserItem.style.color = '#7f9ec4'
        // window.selectUserItem.style.background = 'linear-gradient(rgb(220, 232, 246), rgb(181 215 236))';
        window.selectUserItem.style.background = null;
        // this.style.color = '#c9d7e7';
        this.style.background = 'linear-gradient(rgb(212 235 254), rgb(239 249 255))';
        window.selectUserItem = this;
    }

    fetch("http://" + address + ":" + port + "/map/Index/secrchUserBind?" + useName)
        .then(Response => Response.json())
        .then(text => {
            if(! e.target.classList.contains('item0')){
                return
            }
            if (text['e']==='true'){
                selectUserChat(useName)
            }else {
                let showFile = document.querySelector('#showFile')
                clearEleChildren(showFile)
            }
            if(useName===localUser){
                getSelfChat(useName)
            }
        })
}
async function addDragBind(b, useName, nickName) {
    lodahtml(b)
    scrollToEle(document.querySelector('.viewArea').parentNode,-navPara-3)
    await fetch("http://" + address + ":" + port + "/map/Index/secrchUserBind?" + useName)
        .then(Response => Response.json())
        .then(text => {
            if (text['e']==='true'){
                if (text['p']!=='-1'){
                    let ld=addLi(useName, nickName, 3)
                    ld["li"].click()
                    ld["div"].click()
                }
            }
        })
}
function removeDragBind(useName) {
    let docs = document.querySelectorAll('.Menulist li');
    for (let d of docs) {
        try {
            if (d.getAttribute('username') === useName) {
                delLi1.bind(d)()
            }
        } catch (e) {
        }
    }
}
function removeUserBind() {
    let useName=window.selectUserItem.dataset.username
    fetch("http://" + address + ":" + port + "/map/Index/unLinkUser?" +useName )
        .then(Response => Response.text())
        .then(text => {
            if (text ==='OK'){
                removeDragBind(useName)
                let docs = document.querySelectorAll('.binduser');
                for (let d of docs) {
                    try {
                        if (d.querySelector('.tx').textContent === useName) {
                            d.parentNode.removeChild(d)
                        }
                    } catch (e) {
                    }
                }
            }
        })
}





window.selectUserItem=null;

function displayUCO(e){
    if (window.selectUserItem.dataset.username===null||window.selectUserItem.dataset.username===undefined){
        return false;
    }
    if (window.innerHeight>800){
        document.getElementById("userCO").style.height="320px"
        document.getElementById("contentContrain").style.marginBottom="0px"
        document.querySelector('.viewArea').style.padding='0 3px 0 3px'
    }else {
        document.getElementById("userCO").style.height="30vh"
        document.getElementById("contentContrain").style.marginBottom="0px"
        document.querySelector('.viewArea').style.padding='0 3px 0 3px'
    }

}
function unDisplayUCO(e){
    document.getElementById("userCO").style=null;
    document.getElementById("contentContrain").style.marginBottom=null
    document.querySelector('.viewArea').style.padding=null;
}



document.querySelector('#bindUser').addEventListener('click',bingUser)
function bingUser() {
    let username=window.selectUserItem.dataset.username;
    let id = setTimeout(() => {
        loadstop()
        outTimeNotic("连接请求超时",2000)
    }, 10000);
    loadstart()
    fetch("http://"+address+":" +port+"/map/Index/linkUser?"+username)
        .then((Response)=> {console.log(Response);return Response.ok})
        .then(ok=>{loadstop();  clearTimeout(id);}).catch(e=>{loadstop();  clearTimeout(id);})
}

document.querySelector('#inbindUser').addEventListener('click',inbingUser)
function inbingUser() {
    let username=window.selectUserItem.dataset.username;
        // .then((Response)=> console.log(Response))
    loadstart()
    fetchWithTimeout("http://"+address+":" +port+"/map/Index/inlinkUser?"+username,{}, 5000)
        .then((Response)=> {console.log(Response);return Response.ok})
        .then(ok=>{loadstop()}).catch(e=>{loadstop()})
}
document.querySelector('#returnbindUser').addEventListener('click',returnbingUser)
function returnbingUser() {
    let username=window.selectUserItem.dataset.username;
    loadstart()
    // fetchWithTimeout(fetch("http://"+address+":" +port+"/map/Index/reCallLinkUser?"+username))
    //     .then((Response)=> {console.log(Response);return Response.ok})
    //     .then(ok=>{loadstop()}).catch(e=>{loadstop()})
    fetchWithTimeout("http://"+address+":" +port+"/map/Index/reCallLinkUser?"+username,{},5000)
        .then((Response)=> {console.log(Response);return Response.ok})
        .then(ok=>{loadstop()}).catch(e=>{loadstop()})

    // let con=new AbortController();
    // let signal=con.signal;
    // let fetchEvent=fetch("http://"+address+":" +port+"/map/Index/reCallLinkUser&"+username,{signal})
    // fetchWithTimeout(fetchEvent,con)
    //     .then((Response)=> {
    //     console.log(Response);
    //     return Response.ok})
    //     .then(ok=>{
    //         loadstop()})
    //     .catch(e=>{
    //         console.log(e);
    //         loadstop()})
}

document.querySelector('#setPermis').addEventListener('click',setPermiss)
function setPermiss(e) {
    let param={}
    if (window.localUserName ===window.selectUserItem.dataset.username){
        return
    }
    param['u']=window.selectUserItem.dataset.username
    param['p']=1
    fetch("http://" + address + ":" + port + "/map/Index/setUserPermiss?" + JSON.stringify(param))
    .then(Response => Response.json())
    .then(text => {
    })
}
document.querySelector('#usetPermis').addEventListener('click',usetPermiss)
function usetPermiss(e) {
    let param={}
    if (window.localUserName ===window.selectUserItem.dataset.username){
        return
    }
    param['u']=window.selectUserItem.dataset.username
    param['p']=-1
    fetch("http://" + address + ":" + port + "/map/Index/UnUserPermiss?" + JSON.stringify(param))
        .then(Response => Response.json())
        .then(text => {

        })
}

document.querySelector('#getUi').addEventListener('click',getUi)
function getUi(e) {
    if (window.localUserName ===window.selectUserItem.dataset.username){
        return
    }
    fetch("http://" + address + ":" + port + "/map/Index/getUI?" + window.selectUserItem.dataset.username)
        .then(Response => Response.json())
        .then(text => {
            showText1(text)
        })
}


document.querySelector('#proxybutton').addEventListener('click',openProxy)
let element_proxy = document.getElementById('proxy_page');
let proxy_load_display_state=true;
function openProxy() {
    if (element_proxy.childElementCount > 0) {
        if (proxy_load_display_state) {
            element_proxy.style.display = 'none'
        } else {
            element_proxy.style.display ='flex'
        }
        proxy_load_display_state=!proxy_load_display_state
    } else {
        $('#proxy_page').load('htmls/proxy.html');
    }
}

let getUserS=document.getElementById('userShow')
getUserS.addEventListener("click",getUserShow)

function getUserShow(ele) {
    loadstart()
    fetch("http://"+address+":" +port+"/map/userShow/get?"+window.selectUserItem.dataset.username)
    .then((Response)=> {
        let body=document.querySelector(".create")
        let ssss=document.createElement("div")
        ssss.setAttribute("class","textbox1");
        ssss.innerText='成功'
        body.appendChild(ssss);
        window.setTimeout(function () {
            body.removeChild(ssss);
        },1500)
        console.log(Response)})
    .then(()=>{
    // getUserOfShow()
        getUserOfShowClass()
        loadstop()
    }).catch(e=>{
        loadstop()
    })
}
let getUserT=document.getElementById('userShowT')
// getUserT.addEventListener("click",getUserOfShow)
getUserT.addEventListener("click",getUserOfShowClass)
let userShowFile
function getUserOfShowClass(ele) {
    userShowFile=new UserShowFile(getUserOfShowClassClose,
        window.selectUserItem.dataset.username,
        document.querySelector("#showFile"),
        "/map/userShow/getshowT?",
        "/map/userShow/getShowPath?",
        "/map/userShow/getShowFile")
    userShowFile.getUserOfShow()
}
function getUserOfShowClassClose() {
    userShowFile=null
}


function getUserOfShow(ele) {
    let para={}
    para["u"]=window.selectUserItem.dataset.username
    para["s"]=0
    para["l"]=30
    para=JSON.stringify(para)
    fetch("http://"+address+":" +port+"/map/userShow/getshowT?"+window.selectUserItem.dataset.username,{
        method: 'post',body:para
    }).then((Response)=> {
        let body=document.querySelector(".create")
        let ssss=document.createElement("div")
        ssss.setAttribute("class","textbox1");
        ssss.innerText='成功'
        body.appendChild(ssss);
        window.setTimeout(function () {
            body.removeChild(ssss);
        },1500)
        console.log(Response)
        return Response.json()
    }).then(jsons=>{
        let show=document.querySelector("#showFile")
        let showCon=document.createElement("div")
        showCon.setAttribute("id",'userShowCon')
        clearEleChildren(show)
        show.appendChild(showCon)
        for (let data of jsons){
            let ele
            let file=document.createElement('div')
            file.setAttribute("class",'file')
            let time=document.createElement('p')
            let f=data['f']
            if (f===undefined || f===null){
                f=data['p']
                ele=document.createElement("div")
                ele.setAttribute("class",'p')
            }else {
                ele=document.createElement("div")
                ele.setAttribute("class",'f')
            }
            file.textContent=f
            time.innerText=data['t']
            // time.innerHTML=f
            ele.appendChild(file)
            ele.appendChild(time)
            showCon.appendChild(ele)
        }
        console.log(jsons)
    })
}







let listhorizontally=document.querySelector(" .horizontally").parentNode.parentNode;
listhorizontally.addEventListener('wheel',wheels)
listhorizontally.addEventListener('scroll',showbinduser);
let showbutton=document.querySelector(".showbtn");
showbutton.addEventListener('click',showbtton);
let showscrollstate=true;
let showbinduserDelay= Date.now()
let recordeVtScrollTop=null
let reloadBindListTime=Date.now()
function showbinduser(e) {
    e.preventDefault()
    e.stopPropagation()

    let vl = document.querySelector('.vertically')
    if(vl.style.display==='none'){
        if(recordeVtScrollTop===null){
            recordeVtScrollTop = contextContainer.scrollTop-140;
        }
        return false
    }

    let timeNow = Date.now()
    if ((timeNow -reloadBindListTime)<800){return}
    reloadBindListTime  =timeNow;
    let nod=e.target;
    if ((Date.now()-showbinduserDelay)<500){
        showbinduserDelay=  Date.now()
        return;
    }

    showbinduserDelay= Date.now()
    let nod1=this;
    // let len=(nod1.scrollLeft+nod1.clientWidth)-nod1.scrollWidth;
    let len=nod1.scrollHeight-(nod1.scrollTop+nod1.clientTop);
    // if (len<=-40){return;}
    let br=nod.getBoundingClientRect()

    if (len<=br.height){
        return;
    }

     fetch("http://" + address + ":" + port + "/map/Index/Bindlist" )
        .then((Response) => Response.json())
        .then((json)=>{
            reloadBindList(json)
        })
}


function reloadBindList(json) {
    let user;
    let listhorEle = document.querySelector(".vertically .horizontally");
    listhorEle.innerHTML='';
    // let cs = window.getComputedStyle(listhorizontally);
    // listhorizontally.childNodes.length;
    let keys=Object.keys(json);
    // cs.setProperty('width',length*66+'px');
    // const keys = Object.keys(json);
    // for (let i = keys.length - 1; i >= 0; i--) {
    //     const key = keys[i];
    //     console.log(key, json[key]);
    // }

    let  username
    let  node
    let index
    let ip_port;
    for (index in json){
        user=json[index];
        let usernode=createnodeclass('binduser',"");
        const observer = new IntersectionObserver((entries, observerInstance) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    console.log('元素首次可见，执行一次性操作！');
                    // 执行动画等操作...
                    getheadpic(usernode,user['username']);
                    // 操作完成后，立即断开观察，因为只需要触发一次
                    observerInstance.disconnect();
                }
            });
        });
        observer.observe(usernode);
        // usernode.addEventListener('click',userPageFun)
        enableLongPress(usernode,userPageFun.bind(usernode), (e) =>selectUserChat(user['username']), 500)
        let nickname=user['nickName'];
        if (nickname===null||nickname===undefined){
            node=createnodeclass('txn',"");
        }else {
            node=createnodeclass('txn',nickname);
        }

        username=createnodeclass('tx',user['username']);
        ip_port=createnodeclass('pp',user['address']+':'+user['port']);
        usernode.appendChild(node);
        usernode.appendChild(username);
        usernode.appendChild(ip_port);



        listhorEle.appendChild(usernode);
        usernode.onclick = selectUserBindlist
        usernode.addEventListener('click',displayUCO)
    }
    let  usernode
    if (index<9){
        // let hs=' <div class="binduser"><div class="txn"> null</div></div>'
        // listhorizontally.innerHTML=hs+hs+hs+hs+hs+hs;
        for (let i = 0; i <9-index ; i++) {
            node =createnodeclass('txn',"");
            usernode=createnodeclass('binduser',"");
            username=createnodeclass('tx',"");
            usernode.appendChild(node);
            usernode.appendChild(username);
            listhorEle.appendChild(usernode);
        }
    }
    node =createnodeclass('txn',"");
    usernode=createnodeclass('binduser',"");
    username=createnodeclass('tx',"到达尽头了");
    usernode.appendChild(node);
    usernode.appendChild(username);
    listhorEle.appendChild(usernode);
}


function getheadpic(node,u){
    fetch("http://"+address+":" +port+"/map/Index/headPicture?"+u).
    then((response)=> {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.blob(); // 或者 response.arrayBuffer()
    }).then(imgbuffer=>{
        console.log("http://"+address+":" +port+"/map/Index/headPicture?"+u)
        const objectURL = URL.createObjectURL(imgbuffer);
        const img = document.createElement('img');
        img.setAttribute('class','pic')
        img.src = objectURL;
        img.onload = function() {
            // 计算缩放比例（长边限制为 100px）
            // 获取父元素的宽高（考虑 padding，使用 clientWidth/clientHeight）
            const parentHeight = node.clientHeight;

            // 计算最大允许的宽高（父元素尺寸的 70%）
            const maxSize = parentHeight * 0.6;

            // 原始图片尺寸
            const { width: originalWidth, height: originalHeight } = img;
            const { width, height } = img;
            let scaledWidth, scaledHeight;

            if (width > height) {
                // 宽大于高，以宽度为基准缩放
                scaledWidth = Math.min(width, maxSize);
                scaledHeight = height * (scaledWidth / width);
            } else {
                // 高大于等于宽，以高度为基准缩放
                scaledHeight = Math.min(height, maxSize);
                scaledWidth = width * (scaledHeight / height);
            }
            img.style.width = `${maxSize}px`; // 固定宽度为父元素的 70%
            img.style.height = `${maxSize}px`; // 固定高度为正方形（与宽度相同
            img.style.objectFit = "cover"; // 按比例填充并裁剪
            img.style.objectPosition = "center"; // 居中裁剪

            // 3. 设置缩放后的尺寸
            img.width = scaledWidth;
            img.height = scaledHeight;

            // 4. 将 img 插入为父元素的第一个子元素
            node.insertBefore(img, node.firstChild);
            URL.revokeObjectURL(objectURL)
        };

        // 处理图片加载错误（可选）
        img.onerror = function() {
            console.error("图片加载失败:", objectURL);
            URL.revokeObjectURL(objectURL)
        };
        // img.onload=()=>{
        //     URL.revokeObjectURL(objectURL)
        // }
        node.appendChild(img);
    }) .catch(error => {
        console.error('There has been a problem with your fetch operation:', error);
    });
}

function createnodeclass(cls,text) {
    let node=document.createElement('div');
    node.setAttribute('class',cls);
    node.innerText=text;
    return node;
}
function wheels(e){
    //计算鼠标滚轮滚动的距离
    // console.log("scrollLeft", containerWheel.scrollLeft);
    let containerWheel = e.target.parentNode;
    // px = 0.1 * containerWheel.scrollLeft;
    // px2 = e.clientX + px;
    // t0 = e.clientX < leftwidth;

    let v;
    if (e.wheelDelta<0){
        v=true;
    }else {
        v=false
    }
    let i=Math.ceil(Math.abs(e.wheelDelta/(66*2)) );
        // console.log("scrollLeft", containerWheel.scrollLeft);
    if (v){
        // containerWheel.scrollLeft += i*66;
        containerWheel.scrollLeft += i*11;
    }else {
        // containerWheel.scrollLeft += -i*66;
        containerWheel.scrollLeft += -i*11;
    }
    // containerWheel.scrollLeft += -i*66.6;
    //~~containerWheel.style.width.split('px')[0];


}
let showContentState=true

function showbtton(e) {

    let listcontent=document.querySelector("#content-l");
    let listvertically=document.querySelector(".vertically");

    showbutton=document.querySelector(".showbtn")

    let sbu =showbutton.querySelector('.sbuser')
    let sbb =showbutton.querySelector('.sbbind')
    let hid =showbutton.querySelector('.sbhide')
    // if (showContentState){
    //     listcontent.style.display='none'
    //     listvertically.style.display='flex'
    // }else {
    //     listcontent.style.display='initial'
    //     listvertically.style.display='none'
    // }

    if (sbu===e.target){
        if(recordeVtScrollTop===null){
            recordeVtScrollTop = contextContainer.scrollTop;
        }
        listcontent.style.display='initial'
        listvertically.style.display='none'

        contextContainer.scrollTop=recordeVaScrollTop
        recordeVaScrollTop=null
    }
    if (sbb===e.target){
        if(recordeVaScrollTop===null){
            recordeVaScrollTop = contextContainer.scrollTop;
        }

        listcontent.style.display='none'
        listvertically.style.display='flex'

        contextContainer.scrollTop=recordeVtScrollTop
        recordeVtScrollTop=null
        reloadBindListTime  =Date.now();
    }
    if (hid===e.target){
        if(showContentState){
            listcontent=document.querySelector(" #content-ll");
            listcontent.style.width='0px'
            listcontent.style.visibility = 'hidden'
            listcontent.style.transition= 'opacity 0.3s ease, visibility 0.3s ease';

            // e.target.style.left='14px';
            e.target.style.top= '8px'
            e.target.style.visibility = 'visible'
            e.target.style.zIndex= '999'

            // listvertically.style.display='flex'
        }else {
            listcontent=document.querySelector(" #content-ll");
            listcontent.style.visibility = null
            listcontent.style.width=null
            e.target.style.zIndex= null
            e.target.style.left=null;
            e.target.style.top= null
        }
        showContentState= !showContentState
    }


    // return
    //
    // let contentll=document.querySelector("#content-ll");
    // let listcontainer=document.querySelector("#listcontainer");
    // let i=~~listhorizontally.style.width.split('px')[0];
    // if (i<90){
    //     listvertically.style.width='initial'
    //     // listvertically.style.width='100vh'
    //     listhorizontally.style.width='330px';
    //     listcontainer.style.width='740px';
    //     contentll.style.display='none';
    //     // showbutton.style.marginLeft='330px'
    // }else {
    //     listvertically.style.width='72px'
    //     listhorizontally.style.width='66px';
    //     listcontainer.style.width='643px';
    //     contentll.style.display='initial';
    //     // showbutton.style.marginLeft='60px'
    // }
}

let logcatEle=document.querySelector('#logcat')
let logcatEleState=false
logcatEle.addEventListener('mousedown',actviateLogcatEle)
logcatEle.addEventListener('touchstart',actviateLogcatEle)
logcatEle.addEventListener('mouseleave',unActviateLogcatEle)
logcatEle.addEventListener('mouseup',unActviateLogcatEle)
logcatEle.addEventListener('touchend',unActviateLogcatEle)
logcatEle.addEventListener('touchend',unActviateLogcatEle)

function actviateLogcatEle(e) {
    logcatEleState=true
}

function unActviateLogcatEle() {
    logcatEleState=false
}


// //test
// let dingshitask=setInterval(function (){
//     fetch("http://"+address+":" +port+"/map/Index/DataAuto").
//     then((response)=> {
//         if (!response.ok) {
//             throw new Error(`HTTP error! status: ${response.status}`);
//         }
//         return response.json(); // 或者 response.arrayBuffer()
//     }).then((text)=>{
//         if (text!=="" && text !=="{}" && text!==null && text!==undefined){
//             if (logcatEle==null){
//                 let showFile=document.getElementById('showFile')
//                 if (showFile.childElementCount>0){
//                     return
//                 }else {
//                     logcatEle=document.createElement('div')
//                     logcatEle.setAttribute('id','logcat')
//                 }
//             }
//             clearEleChildren(logcatEle)
//             for (let b of text){
//                 let p=document.createElement('p')
//                 p.textContent=b;
//                 logcatEle.appendChild(p)
//             }
//             // logcatEle.innerHTML=text
//         }else {
//             let p=document.createElement('p')
//             p.textContent="无任务";
//             logcatEle.appendChild(p)
//             // logcatEle.innerHTML= "无任务"
//         }
//     }).catch(e=>{
//         // 错误处理
//         let error;
//         if (e instanceof TypeError) { // 网络错误，例如 DNS 解析失败或服务器未响应
//             error = 'Network error.';
//         } else if (e instanceof SyntaxError) {// JSON 解析错误
//             error = 'JSON parsing error.';
//         } else {// HTTP 错误或其他错误
//             error = e.message;
//         }
//         console.error(error);
//         console.error(error.stack); // 打印堆栈跟踪，如果可用
//         logcatEle.innerHTML=error
//     })
// },80000);


// setInterval(()=>{
//     if (logcatEleState){
//         return
//     }
//     fetch("http://"+address+":" +port+"/map/Andriod/getLogCat")
//         .then((Response)=> {
//         if (!response.ok) {
//             logcatEle.textContent='Network response was not ok';
//         }
//         return response.text(); // 或者 response.arrayBuffer()
//     }).then(data=>{
//         if (logcatEle.textContent.length<3000){
//             logcatEle.textContent=logcatEle.textContent+'/n'+data
//         }else {
//             logcatEle.textContent=data
//         }
//     })
// },10000)
