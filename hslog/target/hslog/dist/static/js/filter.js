export function formatters(val, format,obj) {
    if (typeof (format) === 'function') {
        return format(val,obj)
    } else return val
}
