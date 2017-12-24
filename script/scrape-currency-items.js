// From http://www.pathofexile.com/trade/exchange

var commands = '';
var foundUrls = [];
$('.exchange-filter-item').each(function() {
    var url = $(this).find('img').attr('src');
    url = url.substring(0, url.indexOf('?'));

    if (!foundUrls.includes(url)) {
        commands += 'wget ' + url + "\n";
        foundUrls.push(url)
    }
});
console.log(commands);


