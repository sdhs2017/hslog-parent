const resultComps = {}
let requireComponent = require.context(
    './', // 在当前目录下查找
    true, // 遍历子文件夹
    /\.vue$/ // 正则匹配 以 .vue结尾的文件
)
requireComponent.keys().forEach(fileName => {
    let comp = requireComponent(fileName)
    //console.log(fileName.split('/'))
    resultComps[fileName.replace(/^\.\/(.*)\.\w+$/, '$1').split('/')[2]] = comp.default

})
export default resultComps
