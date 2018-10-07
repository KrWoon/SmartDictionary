var express = require('express');
var app = express();

var client_id = 'W8zmbB6bAGyXWmlr7d8O';
var client_secret = 'u8yw58ggR8';

app.get('/search/encyc', function (req, res) {
   var api_url = 'https://openapi.naver.com/v1/search/encyc?query=' + encodeURI(req.query.query); // json 결과
   var request = require('request');
   var options = {
       url: api_url,
       headers: {'X-Naver-Client-Id':client_id, 'X-Naver-Client-Secret': client_secret}
    };

   request.get(options, function (error, response, body) {
     if (!error && response.statusCode == 200) {
       res.writeHead(200, {'Content-Type': 'text/json;charset=utf-8'});
       res.end(body);
       
       var result = JSON.parse(body);
       console.log("\nlink:", result.items[0].link);
       console.log("description:", result.items[0].description);
     } else {
       res.status(response.statusCode).end();
       console.log('error = ' + response.statusCode);
     }
   });

 });

 app.listen(3000, function () {
   console.log('http://127.0.0.1:3000/search/encyc?query= app listening on port 3000!');
 });

 