/*
* 柱状图数据处理
* obj:[
*  {k1:v1},
*  {k2:v2}
* ]
* */
function barDataFunc(arr) {
    const xVal = [];//x轴数据
    const yVal = [];//y轴数据
    for(const i in arr){
        let obj = arr[i]
        for (const j in obj){
            xVal.push(j);
            yVal.push(obj[j])
        }
    }
    return [xVal,yVal]
}
/*
* 饼图数据处理
* obj:[
*  {k1:v1},
*  {k2:v2}
* ]
* */
function pieDataFunc(arr) {
    const pieVAl = [];//饼图数据
    for(const i in arr){
        let obj = arr[i]
        for (const j in obj){
            const pieObj = {};
            pieObj.value = obj[j];
            pieObj.name = j;
            pieVAl.push(pieObj)
        }
    }
    return pieVAl
}

/*
* 折线图数据处理
* obj:[
*   {count:'',hour:''}
* ]
* */
function lineDataFunc(obj) {
    let xVal = [];
    let yVal = [];
    for(let i in obj){
        yVal.push(obj[i].count)
        xVal.push(obj[i].hour)
    }
    return [xVal,yVal]
}

export {
    barDataFunc,
    pieDataFunc,
    lineDataFunc
}
