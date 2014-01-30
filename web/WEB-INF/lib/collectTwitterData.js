/**
 * Module dependencies.
 */
var format = require('util').format,
	fs  = require("fs"),
	express = require('express'),
	mongojs = require('mongojs'),
	dateFormat = require('dateformat');



var filename = 'tweets.txt';
var db = mongojs('mongodb://127.0.0.1:27017/twitter');
var tweet = db.collection('tweet');


var app = express();

app.get('/search', function(req, res){
	console.log(req.query.search);
	console.log(req.query.from);
	console.log(req.query.to);
	console.log(req.query.part);
	console.log(req.query.parts);
	var created_at_query = null;
	if(req.query.from) {
		if(req.query.to) {
			created_at_query = {$gt: req.query.from, $lt: req.query.to};
		} else {
			created_at_query = {$gt: req.query.from};
		}
	} else {
		if(req.query.to) {
			created_at_query = {$lt: req.query.to};
		} else {
			created_at_query = null;
		}
	}
	var query = null;
	if (created_at_query) {
		query = { search: "\"" + req.query.search + "\"", filter: {created_at : created_at_query}, limit :10000, project: { text: 1, _id: 0 } };
	} else {
		query = { search: "\"" + req.query.search + "\"", limit :10000, project: { text: 1, _id: 0 } };
	}

	tweet.runCommand( "text", query , function(err, docs) {
		var range = (docs.results.length / req.query.parts);
		var startPoint = range * req.query.part;
		console.log(range);
		console.log(docs.results.length);
		console.log(docs.results.slice(startPoint - range,startPoint).length);
		res.send(docs.results.slice(startPoint - range,startPoint));
	});

//	tweet.find(query).sort({created_at: -1},function(err, docs) {
//		var range = (docs.length / req.query.parts);
//		var startPoint = range * req.query.part;
//		console.log(range);
//		console.log(docs.length);
//		console.log(docs.slice(startPoint - range,startPoint).length);
//		res.send(docs.slice(startPoint - range,startPoint));
//	});
});

app.get('/clearDB', function(req, res) {
	tweet.remove(function(err, result) {
		res.send('done');
	});
});

app.get('/countTweets', function(req, res) {
	tweet.runCommand('count', function(err, result) {
		res.send(result);
	});
});

app.get('/loadDB', function(req, res){

	require('readline').createInterface({
		input: fs.createReadStream(filename),
		terminal: false
	}).on('line', function(line){
			if (line.length > 50) {
//				console.log(JSON.parse(line).created_at.date().format());
//				var now = new Date(Date.parse(JSON.parse(line).created_at));
//				console.log(now);
				var parsedLine = JSON.parse(line);
				parsedLine.created_at = dateFormat(JSON.parse(line).created_at, "yyyy-mm-dd HH:MM:ss");
				tweet.save(parsedLine);
			}
			tweet.runCommand('count', function(err, res) {
				console.log(res);
			});

		});
});



app.listen(3001);
console.log('Listening on port 3001');