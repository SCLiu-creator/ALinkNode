fetch("").then((res)=>res.text()).then(console.log);

const res1=await fetch("")
const ht=await res1.text()
console.log(ht)

const postdata={
    username:""
}

const headers={
    token:"",
    "Content-Type":"application/json"
}

fetch("http:",{
    method:"POST",
    headers,
    body:JSON.stringify(postdata)
})

const forms=new FormData()
forms.append("name","aaaaa")
forms.append("id",12)
forms.append("course_file",file)

fetch("",{
    method:"POST",
    body:forms
})



fetch(url).then((ress)=>{
    const reader=ress.body.getReader()
})


const controller=new AbortController()
fetch(url,{
    signal:controller.signal
})

controller.abort;


fetch(url,{
    signal:AbortSignal.timeout(5000)
}).catch((e)=>{
    if(e.username ==="TimeoutError"){
        console.log("time out")
    }
})


const response=await fetch(url)
const reader2=response.body.getReader()
const total=+response.headers.get("Content-Lenght")
let revices=0
while(true){
    const {done,value}=await reader2.read()
    if(done){
        break
    }

    revices+=value.length
    const percent=Math.round((revices*100)/total)
    console.log("jd :${percent}%")

}