let viewFileArray = [];
let viewBlockArray = [];

function viewFile(element) {
    // 遍历元素的祖先元素
    let dragelement = draggableElement
    let target = dragelement.dataset.target;
    let absolute = dragelement.dataset.absolute;
    let abpath = absolute + target
    if (dataShowFile[abpath] !== null || dataShowFile[abpath] !== undefined) {
        dataShowFile = {}
        let obj = {}
        obj['ab'] = abpath
        obj['name'] = window.username
        // let abs=ab.split('/')
        obj['path'] = target
        let param = JSON.stringify(obj)
        fetch("http://" + address + ":" + port + "/map/show/cPathList1?" + param)
            .then((Response) => Response.json())
            .then((json) => {
                console.log(json)
                let data = {}
                data['name'] = window.username;
                let review = []
                // let keys = Object.keys(json);
                let fileView = document.createElement("div");
                fileView.setAttribute('class', 'fileView');
                fileView.dataset.username=window.username
                fileView.dataset.ab=abpath

                let divcontains = document.querySelector("#viewFileCon")

                let del = document.createElement("div");
                del.setAttribute('class', 'upParent')
                del.addEventListener('click', upbutton)
                fileView.appendChild(del)
                // let w= document.documentElement.clientWidth-20
                // // let ww=w/100
                // let wc=(w%100)/(w/100)
                // del.style.width=100+wc+'px'
                // del.style.height=100+wc+'px'
                // let w = document.documentElement.clientWidth - 20
                // let numh = document.documentElement.clientHeight / 100
                let w = divcontains.clientWidth
                let numh = divcontains.clientHeight / 100
                // let ww=w/100
                // let numw = Math.trunc(w / 100)
                let numw  = Math.floor(w / 100)
                const remainingSpace = w % 100; // 剩余空间
                const wc = remainingSpace / numw; // 平均分配剩余空间给每个子元素

                del.style.width = 100 + wc + 'px'
                del.style.height = 100 + wc + 'px'

                // 创建 ResizeObserver 实例
                const resizeObserver = new ResizeObserver((entries) => {
                    for (let entry of entries) {
                        const { width, height } = entry.contentRect; // 获取新尺寸
                        console.log('容器尺寸变化:', width, height);
                        // 触发自定义事件或执行逻辑
                        let w = divcontains.clientWidth
                        let numItems  = Math.floor(w / 100)
                        const remainingSpace = w % 100; // 剩余空间
                        const wc = remainingSpace / (numItems+1); // 平均分配剩余空间给每个子元素

                        del.style.width = 100 + wc + 'px'
                        del.style.height = 100 + wc + 'px'

                        var elements = document.querySelectorAll('.fileView div');
                        elements.forEach(function (ele) {
                            // 获取元素的文本内容并计算其长度
                            // 设置元素的宽度
                            ele.style.width = 100 + wc + 'px'
                            ele.style.height = 100 + wc + 'px'
                        });
                    }
                });
                // 开始监听容器
                resizeObserver.observe(divcontains, {
                    box: 'content-box', // 可选：'border-box' | 'content-box' | 'device-pixel-content-box'
                });
                const intervalId = setInterval(() => {
                    var element = document.querySelector('.fileView div');
                    if (element==null){
                        clearInterval(intervalId)
                        return
                    }
                    const num = parseFloat(element.style.width);
                    let v = w%num
                    let v1 = w/num
                    // v = Math.trunc(v)
                    if (v>v1){
                        console.log("reset")
                        w = divcontains.clientWidth
                        let numItems  = Math.floor((w) / 100)
                        const remainingSpace = (w) % 100; // 剩余空间
                        const wc = Math.trunc(remainingSpace / numItems); // 平均分配剩余空间给每个子元素

                        del.style.width = 100 + wc + 'px'
                        del.style.height = 100 + wc + 'px'

                        var elements = document.querySelectorAll('.fileView div');
                        elements.forEach(function (ele) {
                            // 获取元素的文本内容并计算其长度
                            // 设置元素的宽度
                            ele.style.width = 100 + wc + 'px'
                            ele.style.height = 100 + wc + 'px'
                        });
                    }
                }, 300); // 每 100ms 检查一次


                // let div = document.createElement("div")
                let ss;
                let divn;
                let url = "http://" + address + ":" + port + "/map/ActionCloude/reViewPic?"
                let p;
                for (let key in json) {
                    divn = document.createElement("div")
                    if (json[key] === 'f') {
                        divn.setAttribute('class', 'file')
                        divn.setAttribute('filename', key)
                        divn.addEventListener('click', openCloudeFile)

                        data['file'] = key
                        if (key.toLowerCase().includes('.jpg') ||
                            key.toLowerCase().includes('.png') ||
                            key.toLowerCase().includes('.jpeg')||
                            key.toLowerCase().includes('.webp')
                            // || key.includes('.heic') ||
                            // key.includes('.HEIC')
                        ) {
                            // let d = JSON.stringify(data)
                            // divn.style.backgroundImage= `url("${url+d}")`
                            // let img=document.createElement('img')
                            // img.setAttribute('src',url+d)
                            // divn.appendChild(img)
                            // img.setAttribute('alt',key)
                        }
                    } else {
                        divn.setAttribute('class', 'path')
                        divn.setAttribute('pathname', key)
                        divn.addEventListener('click', vieWCloudePathListson)
                    }
                    review[review.length] = divn
                    p = document.createElement('div')
                    p.setAttribute('class', 'fileTxt')
                    ss = key.split('/')
                    p.textContent = ss[ss.length - 1]
                    divn.appendChild(p)

                    // divn.innerText =
                    divn.dataset.abpath = key
                    fileView.appendChild(divn)
                }

                let cla = {
                    arr: review,

                    loadimg() {
                        let r = fileView.scrollTop / fileView.scrollHeight
                        if (Number.isNaN(r)) {
                            r = 0;
                        }
                        let st = r * this.arr.length

                        if (st === 0) {
                            if (this.arr.length < 1) {
                                return
                            } else {
                                st = 1;
                            }
                        }
                        let ara = null
                        let num = Math.floor(numw * numh)
                        if (this.arr.length < num) {
                            ara = new Array(this.arr.length)
                            let j=0;
                            for (let ele of this.arr) {
                                let s = ele.dataset.s
                                if (s === '0') {
                                    continue
                                } else {
                                    ele.dataset.s = '0'
                                }
                                ara[j] = ele
                                j++;
                            }
                        } else {
                            ara = new Array(num)
                            let num05 = Math.floor(num / 2)
                            if (st < num05) {
                                st = num05
                            }
                            if (st > (this.arr.length - num05)) {
                                st = this.arr.length - num05
                            }
                            st = Math.floor(st - num05)
                            let j=0;
                            for (let i = 0; i < num; i++) {
                                if (review[i + st].dataset.s === '0') {
                                    continue
                                } else {
                                    review[i + st].dataset.s = '0'
                                }
                                ara[j] = review[i + st]
                                j++;
                            }
                        }

                        let runImg=async function* () {
                    /*        let result = iterator.next();
                            while (!result.done) {
                                console.log(result.value); // 输出当前元素
                                myArray.push(myArray.length + 1); // 添加新元素
                                iterator = createArrayIterator(myArray); // 重新创建生成器实例
                                result = iterator.next(); // 获取下一个元素
                            }*/

                            for (let ele of ara) {
                                if (ele === undefined) {
                                    continue
                                }
                                if (ele.querySelector('img') !== null) {
                                    continue
                                }

                                let filenema=ele.getAttribute('filename')
                                if (filenema===null){
                                    continue
                                }
                                if (!(filenema.includes('.jpg') ||
                                    filenema.includes('.webp') ||
                                    filenema.includes('.png') ||
                                    filenema.includes('.jpeg'))) {
                                    continue
                                }

                                if (fileView.parentNode === null || fileView.parentNode === undefined) {
                                    return false
                                }
                                if (fileView.style.display !== 'flex' && fileView.style.display !== '') {
                                    yield;
                                }
                                let img = document.createElement('img')
                                // img.setAttribute('src',url+d)
                                ele.appendChild(img)
                                addDragLoader(ele)
                                data['file'] = ele.getAttribute('filename')
                                let d = JSON.stringify(data)

                                // img.setAttribute('alt', data['file'])
                                try {
                                    let purl = await loadDragFB(url + d);
                                    img.src = purl;
                                    img.onload = () => {
                                        URL.revokeObjectURL(purl);
                                    };
                                } catch (e) {
                                    console.log(e)
                                } finally {
                                    removeDragLoader(ele)
                                }
                            }
                        }
                        let nri=viewBlockArray[viewFileArray.length] = runImg()
                        new Promise((() => {
                            console.log('loaderImg')
                            nri.next()
                            console.log('loaderOver')
                        })).then(r => console.log('loaderImgOver'));
                        //     .then((ele)=>{
                        //
                        // },error=>{
                        //     console.log(error)
                        //     return
                        // })).then(ele => {
                        //     console.log("img")
                        //     // 执行你需要的操作
                        // });
                        console.log('csm')
                    }
                }

                fileView.addEventListener('scroll', cla['loadimg'].bind(cla))

                // fileView.addEventListener('scroll',function () {
                //     console.log('cs')
                // })
                divcontains.dataset.ab = abpath
                divcontains.dataset.name = window.username

                divcontains.appendChild(fileView)
                viewFileArray[viewFileArray.length] = fileView
                dataShowFile[0] = target;

                // for (let k in viewFileArray) {
                //     viewFileArray[k].style.display='none'
                // }
                viewFileArray[viewFileArray.length - 1].style.display = 'flex'
                opertaContainer.style.width = '100vw'

                var elements = document.querySelectorAll('.fileView div');
                elements.forEach(function (ele) {
                    // 获取元素的文本内容并计算其长度
                    // 设置元素的宽度
                    ele.style.width = 100 + wc + 'px'
                    ele.style.height = 100 + wc + 'px'
                });
                //
                // let nextRun = async function* () {
                //     for (let ele of review) {
                //         // review.every(async function (ele) {
                //         data['file'] = ele.getAttribute('filename')
                //         let d = JSON.stringify(data)
                //         let img = document.createElement('img')
                //         // img.setAttribute('src',url+d)
                //         ele.appendChild(img)
                //         img.setAttribute('alt', data['file'])
                //
                //         if (fileView.parentNode === null || fileView.parentNode === undefined) {
                //             return false
                //         }
                //         if (fileView.style.display !== 'flex' && fileView.style.display !== '') {
                //             yield;
                //         }
                //
                //         let purl = await loadDragFB(url + d);
                //         img.src = purl;
                //         img.onload = () => {
                //             URL.revokeObjectURL(purl);
                //         };
                //
                //         // return true
                //         // ele.style.backgroundImage = `url("${url+d}")`
                //         // ele.style.backgroundRepeat = "no-repeat";
                //         // ele.style.backgroundSize = "cover"; // 或者 "contain", "100% 100%", 等
                //         // ele.style.backgroundSize="center"
                //     }
                // }
                // let nRun = nextRun()
                // cla['loadimg']()
                // nRun.next()
                // viewBlockArray[viewFileArray.length]=nRun
                // scrollToEle(opertaContainer, 4)
                cla['loadimg']()


                function select(stri) {
                    let arr=[]
                    for (let ele of review) {
                        try {
                            let name=ele.getAttribute('filename')
                            if (name==null){
                                name=ele.getAttribute('pathname')
                            }
                            if (name.includes(stri)){
                                arr.push(ele)
                            }
                            fileView.removeChild(ele)
                        }catch (e) {
                            console.log(e)
                        }

                    }
                    cla['arr']=arr
                    for (let ele of arr){
                        fileView.appendChild(ele)
                    }
                }
                selectFun=select
                function sortu(b) {
                    // 获取所有子元素并转换为数组
                    let childNodes = Array.from(fileView.children);

                    // 对子元素进行排序和筛选
                    // 假设我们根据子元素的data-value属性进行排序，并且筛选出属性值大于1的元素
                    if (false){
                        childNodes = childNodes
                            .sort((a, b) => {
                                let aa=a.querySelector("P").textContent

                                let bb=a.querySelector("P").textContent
                                if (aa==null||bb==null){
                                    return 0
                                }
                                return aa.localeCompare(bb)
                            })
                    }else {
                        childNodes = childNodes
                            .sort((a, b) => {
                                let aa=a.getAttribute('filename')
                                if(aa==null){
                                    aa=a.getAttribute('pathname')
                                }
                                let bb=b.getAttribute('filename')
                                if(bb==null){
                                    bb=b.getAttribute('pathname')
                                }
                                if (aa==null||bb==null){
                                    return 0
                                }
                                return aa.localeCompare(bb)
                            })
                    }

                    // .filter(child => child.getAttribute('name').includes(requiredString));
                    while (fileView.firstChild) {
                        fileView.removeChild(fileView.firstChild);
                    }
                    childNodes.forEach(child => fileView.appendChild(child));
                }
                function sortd(b) {
                    // 获取所有子元素并转换为数组
                    let childNodes = Array.from(fileView.children);

                    // 对子元素进行排序和筛选
                    // 假设我们根据子元素的data-value属性进行排序，并且筛选出属性值大于1的元素
                    if(false){
                        childNodes = childNodes
                            .sort((a, b) => {
                                let aa=a.querySelector("P").textContent
                                let bb=a.querySelector("P").textContent
                                if (aa==null||bb==null){
                                    return 0
                                }
                                return bb.localeCompare(aa)
                            })
                    }else {
                        childNodes = childNodes
                            .sort((a, b) => {
                                let aa=a.getAttribute('filename')
                                if(aa==null){
                                    aa=a.getAttribute('pathname')
                                }
                                let bb=b.getAttribute('filename')
                                if(bb==null){
                                    bb=b.getAttribute('pathname')
                                }
                                if (aa==null||bb==null){
                                    return 0
                                }
                                return bb.localeCompare(aa)
                            })
                    }

                    // .filter(child => child.getAttribute('name').includes(requiredString));
                    while (fileView.firstChild) {
                        fileView.removeChild(fileView.firstChild);
                    }
                    childNodes.forEach(child => fileView.appendChild(child));
                }
                sortFund=sortd
                sortFunu=sortu


                addDragTouchParent(fileView,touchEvenFunView,touchMoveEvenFunView,touchLongFun,['path','file'],
                    document.getElementById('dragbuffer'),
                    "",800)
                addDragMouseParent(fileView,mouseEvenFunView,mouseMoveEvenFunView,mouseLongFun,['path','file'],
                    document.getElementById('dragbuffer'),
                    "",800)
            })
    }
    // 没有找到符合条件的祖先元素
    return false;
}

async function loadDragFB(url) {
    const response = await fetch(url)
    const blob = await response.blob()
    return URL.createObjectURL(blob);
}

function addDragLoader(ele) {
    let load = document.createElement('d')
    let loadb = document.createElement('d')
    load.setAttribute('class', 'loader')
    loadb.setAttribute('class', 'loaddrag')
    "".toLowerCase()
    loadb.appendChild(load)
    ele.appendChild(loadb)
}

function removeDragLoader(ele) {
    let lg=ele.querySelector('.loaddrag')
    ele.removeChild(lg)
    // let eles = ele.childNodes
    // for (let el of eles) {
    //     if (el.tagName.toLowerCase() === 'd') {
    //         ele.removeChild(el)
    //     }
    // }
}


function vieWCloudePathListson(e) {
    let ele = this.parentNode
    let param = {}
    param['ab'] = ele.parentNode.dataset.ab;
    param['name'] = ele.parentNode.dataset.name;
    let abs = this.dataset.abpath.split('/');
    param['path'] = abs[abs.length - 1];
    loadstart();
    fetch("http://" + address + ":" + port + "/map/show/cPathList", {
        method: 'post',
        body: JSON.stringify(param)
    })
        .then((Response) => Response.json())
        .then((json) => {
            // json=JSON.parse(json)
            console.log(json)
            let data = {}
            data['name'] = window.username;
            let review = []
            // let keys = Object.keys(json);
            let fileView = document.createElement("div");
            fileView.setAttribute('class', 'fileView');
            fileView.dataset.username=window.username
            fileView.dataset.ab=param['ab']

            let divcontains = document.querySelector("#viewFileCon")
            // let w = Number(window.getComputedStyle(divcontains).width.replace('px', '')) - 20
            // let numh = document.documentElement.clientHeight / 100
            let w = divcontains.clientWidth
            let numh = divcontains.clientHeight / 100
            // let ww=w/100
            let numw =Math.trunc(w / 100)
            let wc = ((w % 100)) / (numw+1)

            let del = document.createElement("div");
            del.setAttribute('class', 'upParent')
            del.addEventListener('click', upbutton)
            fileView.appendChild(del)
            del.style.width = 100 + wc + 'px'
            del.style.height = 100 + wc + 'px'
            let ss;
            let divn;
            let url = "http://" + address + ":" + port + "/map/ActionCloude/reViewPic?"
            let p;
            for (let key in json) {
                divn = document.createElement("div")
                if (json[key] === 'f') {
                    divn.setAttribute('class', 'file')
                    divn.setAttribute('filename', key)
                    divn.addEventListener('click', openCloudeFile)
                    data['file'] = key
                } else {
                    divn.setAttribute('class', 'path')
                    divn.setAttribute('pathname', key)
                    divn.addEventListener('click', vieWCloudePathListson)
                }
                review[review.length] = divn
                p = document.createElement('div')
                p.setAttribute('class', 'fileTxt')
                ss = key.split('/')
                p.textContent = ss[ss.length - 1]
                divn.appendChild(p)
                divn.dataset.abpath = key
                fileView.appendChild(divn)
            }
            divcontains.appendChild(fileView)

            for (let k in viewFileArray) {
                viewFileArray[k].style.display = 'none'
            }
            viewFileArray[viewFileArray.length] = fileView

            let elements = document.querySelectorAll('.fileView div');
            elements.forEach(function (ele) {
                // 获取元素的文本内容并计算其长度
                // 设置元素的宽度
                ele.style.width = 100 + wc + 'px'
                ele.style.height = 100 + wc + 'px'
            });
            // review.forEach(function(ele) {
            //     // 获取元素的文本内容并计算其长度
            //     // 设置元素的宽度
            //     data['file'] = ele.getAttribute('filename')
            //     let d = JSON.stringify(data)
            //     let img=document.createElement('img')
            //     img.setAttribute('src',url+d)
            //     ele.appendChild(img)
            //     img.setAttribute('alt', data['file'] )
            // });
            let cla = {
                arr: review,
                numh:numh,
                numw:numw,
                loadimg() {
                    let r = fileView.scrollTop / fileView.scrollHeight
                    if (Number.isNaN(r)) {
                        r = 0;
                    }

                    let st = r * this.arr.length
                    if (st === 0) {
                        if (this.arr.length < 1) {
                            return
                        } else {
                            st = 1;
                        }
                    }

                    let ara = null
                    let num = Math.floor(this.numw * this.numh)
                    if (this.arr.length < num) {
                        ara = new Array(this.arr.length)
                        for (let ele of this.arr) {
                            let s = ele.dataset.s
                            if (s === '0') {
                                continue
                            } else {
                                ele.dataset.s = '0'
                            }
                            ara[ara.length] = ele
                        }
                    } else {
                        ara = new Array(num)
                        let num05 = Math.floor(num / 2)
                        if (st < num05) {
                            st = num05
                        }
                        if (st > (this.arr.length - num05)) {
                            st = this.arr.length - num05
                        }
                        st = Math.floor(st - num05)
                        for (let i = 0; i < num; i++) {
                            if (review[i + st].dataset.s === '0') {
                                continue
                            } else {
                                review[i + st].dataset.s = '0'
                            }
                            ara[ara.length] = review[i + st]
                        }
                    }

                    let runImg=async function* () {
                        for (let ele of ara) {
                            if (ele === undefined) {
                                continue
                            }
                            if (ele.querySelector('img') !== null) {
                                continue
                            }
                            let filenema=ele.getAttribute('filename')
                            if (filenema===null){
                                continue
                            }
                            if (!(filenema.includes('.jpg') ||
                                    filenema.includes('.webp') ||
                                filenema.includes('.png') ||
                                filenema.includes('.jpeg'))) {
                                continue
                            }

                            if (fileView.parentNode === null || fileView.parentNode === undefined) {
                                return false
                            }
                            if (fileView.style.display !== 'flex' && fileView.style.display !== '') {
                                yield;
                            }
                            let img = document.createElement('img')
                            // img.setAttribute('src',url+d)
                            ele.appendChild(img)
                            addDragLoader(ele)
                            data['file'] = ele.getAttribute('filename')
                            let d = JSON.stringify(data)

                            // img.setAttribute('alt', data['file'])
                            try {
                                let purl = await loadDragFB(url + d);
                                img.src = purl;
                                img.onload = () => {
                                    URL.revokeObjectURL(purl);
                                };
                            } catch (e) {
                                console.log(e)
                            } finally {
                                removeDragLoader(ele)
                            }
                        }
                    }
                    let nri=viewBlockArray[viewFileArray.length] = runImg()
                    new Promise((() => nri.next())).then(r => console.log('loaderImgOver'));
                    console.log('csm')
                }
            }

            fileView.addEventListener('scroll', cla['loadimg'].bind(cla))

            function select(stri) {
                let arr=[]
                for (let ele of review) {
                    try {
                        let name=ele.getAttribute('filename')
                        if (name==null){
                            name=ele.getAttribute('pathname')
                        }
                        if (name.includes(stri)){
                            arr.push(ele)
                        }
                        fileView.removeChild(ele)
                    }catch (e) {
                        console.log(e)
                    }
                }
                cla['arr']=arr
                for (let ele of arr){
                    fileView.appendChild(ele)
                }
            }
            selectFun=select
            function sortu() {
                // 获取所有子元素并转换为数组
                let childNodes = Array.from(fileView.children);

                // 对子元素进行排序和筛选
                // 假设我们根据子元素的data-value属性进行排序，并且筛选出属性值大于1的元素
                if (false){
                    childNodes = childNodes
                        .sort((a, b) => {
                            let aa=a.querySelector("P").textContent
                            let bb=a.querySelector("P").textContent
                            if (aa==null||bb==null){
                                return 0
                            }
                            return aa.localeCompare(bb)
                        })
                }else {
                    childNodes = childNodes
                        .sort((a, b) => {
                            let aa=a.getAttribute('filename')
                            if(aa==null){
                                aa=a.getAttribute('pathname')
                            }
                            let bb=b.getAttribute('filename')
                            if(bb==null){
                                bb=b.getAttribute('pathname')
                            }
                            if (aa==null||bb==null){
                                return 0
                            }
                            return aa.localeCompare(bb)
                        })
                }

                    // .filter(child => child.getAttribute('name').includes(requiredString));
                while (fileView.firstChild) {
                    fileView.removeChild(fileView.firstChild);
                }
                childNodes.forEach(child => fileView.appendChild(child));
            }
            function sortd() {
                // 获取所有子元素并转换为数组
                let childNodes = Array.from(fileView.children);

                // 对子元素进行排序和筛选
                // 假设我们根据子元素的data-value属性进行排序，并且筛选出属性值大于1的元素
                if (false){
                    childNodes = childNodes
                        .sort((a, b) => {
                            let aa=a.querySelector("P").textContent
                            let bb=a.querySelector("P").textContent
                            if (aa==null||bb==null){
                                return 0
                            }
                            return bb.localeCompare(aa)
                        })
                }else {
                    childNodes = childNodes
                        .sort((a, b) => {
                            let aa=a.getAttribute('filename')
                            if(aa==null){
                                aa=a.getAttribute('pathname')
                            }
                            let bb=b.getAttribute('filename')
                            if(bb==null){
                                bb=b.getAttribute('pathname')
                            }
                            if (aa==null||bb==null){
                                return 0
                            }
                            return bb.localeCompare(aa)
                        })
                }

                // .filter(child => child.getAttribute('name').includes(requiredString));
                while (fileView.firstChild) {
                    fileView.removeChild(fileView.firstChild);
                }
                childNodes.forEach(child => fileView.appendChild(child));
            }
            sortFund=sortd
            sortFunu=sortu

            // let nextRun = async function* () {
            //     for (let ele of review) {
            //         data['file'] = ele.getAttribute('filename')
            //         let d = JSON.stringify(data)
            //         let img = document.createElement('img')
            //         ele.appendChild(img)
            //         img.setAttribute('alt', data['file'])
            //         if (fileView.parentNode === null || fileView.parentNode === undefined) {
            //             return false
            //         }
            //         if (fileView.style.display !== 'flex' && fileView.style.display !== '') {
            //             yield;
            //         }
            //
            //         await loadDragFB(url + d).then(purl => {
            //             img.src = purl;
            //             img.onload = () => {
            //                 // 图片加载完成，可以释放 object URL
            //                 URL.revokeObjectURL(purl);
            //             };
            //         }).catch(e => {
            //             console.log(e)
            //         })
            //         // return true
            //     }
            // }

            // let nr = nextRun()
            // // nr.next()
            //
            // viewBlockArray[viewBlockArray.length] = nr
            cla['loadimg']()
            loadstart();


            addDragTouchParent(fileView,touchEvenFunView,touchMoveEvenFunView,touchLongFun,['path','file'],
                document.getElementById('dragbuffer'),
                "",1500)
            addDragMouseParent(fileView,mouseEvenFunView,mouseMoveEvenFunView,mouseLongFun,['path','file'],
                document.getElementById('dragbuffer'),
                "",1500)

        }).then(() => {
        loadstop()
    }).catch(e => loadstop())
}

function upbutton(e) {
    this.parentNode.parentNode.removeChild(this.parentNode)
    viewFileArray = viewFileArray.slice(0, -1)
    if (viewFileArray.length === 0) {
        // opertaContainer.style.width='388px'
        opertaContainer.style = null
        scrollToEle(document.querySelector('.dragContainer'))
    } else {
        viewFileArray[viewFileArray.length - 1].style.display = 'flex'
    }
    if (viewBlockArray.length > 1) {
        viewBlockArray = viewBlockArray.slice(0, -1)
        if (viewBlockArray.length > 1) {
            let n = viewBlockArray[viewBlockArray.length - 1];
            try {
                n.next()
            } catch (e) {
                console.log(e)
            }
        }
    }
}


function fitImageToParent(img, parent) {
    const parentWidth = parent.offsetWidth;
    const parentHeight = parent.offsetHeight;
    const imgRatio = img.naturalWidth / img.naturalHeight;
    let imgWidth, imgHeight;

    if (parentWidth / parentHeight > imgRatio) {
        // 父元素更宽，以高度为基准
        imgHeight = parentHeight;
        imgWidth = imgHeight * imgRatio;
    } else {
        // 父元素更高，以宽度为基准
        imgWidth = parentWidth;
        imgHeight = imgWidth / imgRatio;
    }

    img.style.width = `${imgWidth}px`;
    img.style.height = `${imgHeight}px`;
    // 如果需要，还可以添加居中等其他样式
}

// 假设你已经有了对应的img和parent元素
// const img = document.querySelector('img');
// const parent = document.querySelector('.parent');
// fitImageToParent(img, parent);


document.addEventListener('DOMContentLoaded', function () {
    const images = document.querySelectorAll('.image-container img[data-src]');
    const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const img = entry.target;
                // 加载当前图片
                loadImage(img);

                // 尝试加载接下来的两个图片（如果存在）
                const nextImage = img.nextElementSibling?.querySelector('img[data-src]');
                if (nextImage) loadImage(nextImage);

                const nextNextImage = nextImage?.nextElementSibling?.querySelector('img[data-src]');
                if (nextNextImage) loadImage(nextNextImage);

                observer.unobserve(img); // 停止观察当前图片（可选，取决于你是否想再次加载它）
            }
        });
    }, {
        rootMargin: '0px',
        threshold: 0.1 // 当图片有10%进入视口时开始加载
    });

    function loadImage(img) {
        img.src = img.dataset.src;
        img.onload = () => {
            img.removeAttribute('data-src');
        };
        img.onerror = () => {
            img.src = 'path/to/fallback-image.jpg';
        };
    }

    images.forEach(img => {
        observer.observe(img);
    });
});


let loadQueue = Promise.resolve(); // 初始化一个已解决的Promise作为加载队列的起点

document.addEventListener('DOMContentLoaded', function () {
    const lazyImages = document.querySelectorAll('img.lazy');

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                // 将当前图片的加载操作添加到加载队列中
                loadQueue = loadQueue.then(() => {
                    return new Promise((resolve, reject) => {
                        const img = entry.target;
                        img.onload = resolve;
                        img.onerror = reject;

                        // 替换占位符URL为实际的图像URL
                        img.src = img.dataset.src;

                        // 可选：从元素中移除data-src属性以避免重复加载
                        img.removeAttribute('data-src');

                        // 可选：停止观察该元素（如果不再需要）
                        observer.unobserve(img);
                    });
                });
            }
        });
    }, {
        root: null,
        rootMargin: '0px',
        threshold: 0.01
    });

    // 对所有懒加载图像元素应用观察者
    lazyImages.forEach(img => {
        observer.observe(img);
    });
});



function touchEvenFunView(even,dragEle){
    let at=even.changedTouches
    let x=at[at.length-1].clientX
    let y=at[at.length-1].clientY
    let ele=getDragsUnder(x,y)

    if(ele.tagName==='IMG'){
        ele=ele.parentNode
    }
    if (ele.classList.contains('fileTxt')){
        ele=ele.parentNode
    }
    // ||ele.classList.contains('file')
    try {
        if (ele.dataset.abpath===dragEle.dataset.abpath ){
            let co=document.createElement('div')
            co.setAttribute('class','CFPopera')
            let del=document.createElement('div')
            del.setAttribute('class','CFPopDel')
            del.textContent='删除'
            ele.appendChild(co)
            co.appendChild(del)
            let data={}
            data['file']=ele.dataset.abpath
            data['user']=ele.parentNode.parentNode.dataset.name
            data['path']=ele.parentNode.parentNode.dataset.ab
            del.addEventListener('click',function (e) {
                e.stopPropagation()
                e.preventDefault()
                fetch("http://" + address + ":" + port + "/map/ActionCloude/removeCloudeFile"
                    , {
                        method: 'post',
                        body: JSON.stringify(data),
                        headers: {'Content-Type': 'application/json',},}
                ).then(Response=>{
                    if(Response.ok){
                        ele.parentNode.removeChild(ele)
                    }
                })
                console.log('del')
            })
            co.addEventListener('click',function (e) {
                e.stopPropagation()
                e.preventDefault()
                if(e.target===this){
                    e.target.parentNode.removeChild(this)
                }
                console.log('clear')
            })
        }
    }catch (e){
        console.log(e)
    }

    if (ele.classList.contains('path')){
        let f0=ele.dataset.abpath
        let na=ele.parentNode.parentNode.dataset.name
        let ab=dragEle.dataset.abpath
        let na0=dragEle.dataset.name

        let data = {}
        data['name'] = na;
        data['startUser'] = na0;
        data['file'] = ab
        data['path'] = f0
        let d=JSON.stringify(data)
        console.log(JSON.stringify(data))
        fetch("http://" + address + ":" + port + "/map/ActionCloude/moveCloudeFile?" +d
            , {
                method: 'post',
                body: d,
                headers: {'Content-Type': 'application/json',},}
        )
    }
    if (ele.classList.contains('fileView')){
        let f0=ele.dataset.ab
        let na=ele.parentNode.dataset.name
        let ab=dragEle.dataset.abpath
        let na0=dragEle.dataset.name

        let data = {}
        data['name'] = na;
        data['startUser'] = na0;
        data['file'] = ab
        data['path'] = f0
        let d = JSON.stringify(data)
        console.log(JSON.stringify(data))
        fetch("http://" + address + ":" + port + "/map/ActionCloude/moveCloudeFile?" + d
            , {
                method: 'post',
                body: d,
                headers: {'Content-Type': 'application/json',},}
        )
    }
    if (ele.classList.contains('upParent')){
        ele=ele.parentNode
        let f0=ele.dataset.ab
        let na=ele.parentNode.dataset.name
        let ab=dragEle.dataset.abpath
        let na0=dragEle.dataset.name

        let data = {}
        data['name'] = na;
        data['startUser'] = na0;
        data['file'] = ab
        data['path'] = f0
        let d = JSON.stringify(data)
        console.log(JSON.stringify(data))

        fetch("http://" + address + ":" + port + "/map/ActionCloude/moveCloudeFile?" + d
            , {
                method: 'post',
                body: d,
                headers: {'Content-Type': 'application/json',},}
        )
        return true
    }

    if (ele.classList.contains('viewFileCon')){
        let f0=ele.dataset.ab
        let na=ele.dataset.name
        let ab=even.target.dataset.abpath
        let na0=dragEle.dataset.name

        let data = {}
        data['name'] = na;
        data['startUser'] = na0;
        data['file'] = ab
        data['path'] = f0
        let d=JSON.stringify(data)
        console.log(d)
        fetch("http://" + address + ":" + port + "/map/ActionCloude/moveCloudeFile?" + d
            , {
                method: 'post',
                body: d,
                headers: {'Content-Type': 'application/json',},}
        )
    }


    console.log('longtime')
    console.log('longtime')
}

function touchMoveEvenFunView(even){
    let x=even.touches[0].clientX
    let y=even.touches[0].clientY
    let ele=getDragsUnder(x,y)

    if (ele.classList.contains('path')){
        vieWCloudePathListson.bind(ele)()
        return true
    }
    // if (ele.classList.contains('fileTxt')){
    //     ele=ele.parentNode
    //     vieWCloudePathListson.bind(ele)()
    //     return true
    // }
    if (ele.classList.contains('upParent')){
        upbutton.bind(ele)(even)
        // fetch("http://" + address + ":" + port + "/map/ActionCloude/getCloudeFile?" + d
        //     // , {
        //     // method: 'post',
        //     // body: d,
        //     // headers: {'Content-Type': 'application/json',},}
        // )
        return true
    }
    if (ele.classList.contains('menuToggle')){
        if (leftbodystate) {
            leftbody.style.display = 'none'
            // leftbody.style.width='8px'
            rightCon.style.marginLeft = '0px'
            leftbodystate = false;
            navPara=77;
        } else {
            leftbody.style.display = ''
            // leftbody.style.width='8px'
            rightCon.style.marginLeft = '77px'
            leftbodystate = true;
            navPara=0;
        }
        // menuToggle.style.display='flex'
        menuToggle.classList.toggle('active')
        return true
    }
    if (ele.classList.contains('icon')){
        window.username=ele.dataset.username
        lodahtml(true)
        return true
    }
    if (ele.classList.contains('dragleft')){
        draggableElement=ele.cloneNode(true)
        if (viewState){
            isShowFile()
        }else {
            viewFile()
        }
        return true
    }

    console.log('longtime')
}

function mouseEvenFunView(even,dragEle){
    let x=even.clientX
    let y=even.clientY
    let ele=getDragsUnder(x,y)

    if(ele.tagName==='IMG'){
        ele=ele.parentNode
    }
    if (ele.classList.contains('fileTxt')){
        ele=ele.parentNode
    }
    // ||ele.classList.contains('file')
    try {
        if (ele.dataset.abpath===dragEle.dataset.abpath){
            let co=document.createElement('div')
            co.setAttribute('class','CFPopera')
            let del=document.createElement('div')
            del.setAttribute('class','CFPopDel')
            del.textContent='删除'
            ele.appendChild(co)
            co.appendChild(del)
            let data={}
            data['file']=ele.dataset.abpath
            data['user']=ele.parentNode.parentNode.dataset.name
            data['path']=ele.parentNode.parentNode.dataset.ab
            del.addEventListener('click',function (e) {
                e.stopPropagation()
                e.preventDefault()
                fetch("http://" + address + ":" + port + "/map/ActionCloude/removeCloudeFile"
                    , {
                        method: 'post',
                        body: JSON.stringify(data),
                        headers: {'Content-Type': 'application/json',},}
                ).then(Response=>{
                    if(Response.ok){
                        ele.parentNode.removeChild(ele)
                    }
                })
                console.log('del')
            })
            co.addEventListener('click',function (e) {
                e.stopPropagation()
                e.preventDefault()
                if(e.target===this){
                    e.target.parentNode.removeChild(this)
                }
                console.log('clear')
            })
            return
        }
    }catch (e){
        console.log(e)
    }

    if (ele.classList.contains('path')){
        let f0=ele.dataset.abpath
        let na=ele.parentNode.parentNode.dataset.name
        let ab=dragEle.dataset.abpath
        let na0=dragEle.dataset.name

        let data = {}
        data['name'] = na;
        data['startUser'] = na0;
        data['file'] = ab
        data['path'] = f0
        let d = JSON.stringify(data)
        console.log(JSON.stringify(data))
        fetch("http://" + address + ":" + port + "/map/ActionCloude/moveCloudeFile?" + d
            , {
                method: 'post',
                body: d,
                headers: {'Content-Type': 'application/json',},}
        )
    }
    if (ele.classList.contains('fileView')){
        let f0=ele.dataset.ab
        let na=ele.parentNode.dataset.name
        let ab=dragEle.dataset.abpath
        let na0=dragEle.dataset.name

        let data = {}
        data['name'] = na;
        data['startUser'] = na0;
        data['file'] = ab
        data['path'] = f0
        let d = JSON.stringify(data)
        console.log(JSON.stringify(data))
        fetch("http://" + address + ":" + port + "/map/ActionCloude/moveCloudeFile?" + d
            , {
                method: 'post',
                body: d,
                headers: {'Content-Type': 'application/json',},}
        )
    }
    if (ele.classList.contains('upParent')){
        ele=ele.parentNode
        let f0=ele.dataset.ab
        let na=ele.parentNode.dataset.name
        let ab=dragEle.dataset.abpath
        let na0=dragEle.dataset.name

        let data = {}
        data['name'] = na;
        data['startUser'] = na0;
        data['file'] = ab
        data['path'] = f0
        let d = JSON.stringify(data)
        console.log(JSON.stringify(data))

        fetch("http://" + address + ":" + port + "/map/ActionCloude/moveCloudeFile?" + d
            , {
                method: 'post',
                body: d,
                headers: {'Content-Type': 'application/json',},}
        )
        return true
    }

    if (ele.classList.contains('viewFileCon')){
        let f0=ele.dataset.ab
        let na=ele.dataset.name
        let ab=even.target.dataset.abpath
        let na0=dragEle.dataset.name

        let data = {}
        data['name'] = na;
        data['startUser'] = na0;
        data['file'] = ab
        data['path'] = f0
        let d=JSON.stringify(data)
        console.log(d)
        fetch("http://" + address + ":" + port + "/map/ActionCloude/moveCloudeFile?" + d
            , {
                method: 'post',
                body: d,
                headers: {'Content-Type': 'application/json',},}
        ).then(response=>{
            showText("成功")
        })
    }


    console.log('longtime')
    console.log('longtime')
}

function mouseMoveEvenFunView(even){
    let x=even.clientX
    let y=even.clientY
    let ele=getDragsUnder(x,y)

    if (ele.classList.contains('path')){
        vieWCloudePathListson.bind(ele)()
        return true
    }
    // if (ele.classList.contains('fileTxt')){
    //     ele=ele.parentNode
    //     vieWCloudePathListson.bind(ele)()
    //     return true
    // }
    if (ele.classList.contains('upParent')){
        upbutton.bind(ele)(even)
        return true
    }
    if (ele.classList.contains('menuToggle')){
        if (leftbodystate) {
            leftbody.style.display = 'none'
            // leftbody.style.width='8px'
            rightCon.style.marginLeft = '0px'
            leftbodystate = false;
            navPara=77;
        } else {
            leftbody.style.display = ''
            // leftbody.style.width='8px'
            rightCon.style.marginLeft = '77px'
            leftbodystate = true;
            navPara=0;
        }
        // menuToggle.style.display='flex'
        menuToggle.classList.toggle('active')
        return true
    }
    if (ele.classList.contains('icon')){
        window.username=ele.dataset.username
        lodahtml(true)
        return true
    }
    if (ele.classList.contains('dragleft')){
        draggableElement=ele.cloneNode(true)
        if (viewState){
            isShowFile()
        }else {
            viewFile()
        }
        return true
    }

    console.log('longtime')
}
