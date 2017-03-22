
var express = require('express');
var app = express();
var request = require('request');
var async = require('async');


app.get('/getEventsWithWeather/:location', function (req, res) {
    var result={
        'eventsList': []
    };
    var weatherArray =[];
    var weatherRes = '';

    request('http://api.eventful.com/json/events/search?app_key=F5886d5cVJ4M8sXR&location='+req.params.location+'&date=Today', function (error, response, body)
    {
        //Check for error
        if(error){
            return console.log('Error:', error);
        }

        //Check for right status code
        if(response.statusCode !== 200){
            return console.log('Invalid Status Code Returned:', response.statusCode);
        }
        //All is good. Print the body
        body = JSON.parse(body);
        eventRes = body.events.event;

        for(var i=0;i<eventRes.length;i++)
        {
            latitude=eventRes[i].latitude;
            longitude=eventRes[i].longitude;
            event=eventRes[i];

            request('http://api.wunderground.com/api/dc3009047269d125/conditions/q/' + event.latitude + ',' + event.longitude + '.json', function (error, response, body)
            {
                if (error) {
                    console.log(weatherUrl);
                    return console.log('Error weather:', error);
                }

                body1 = JSON.parse(body);
                weatherRes = body1.current_observation.temperature_string;
                weatherArray.push(weatherRes);
                console.log(weatherRes);
            });

            result.eventsList.push({
                'title': event.title,
                'address': event.venue_address,
                'description': event.description,
                'latitude': event.latitude,
                'longitude': event.longitude,
                'start_time':event.start_time,
                'venue_name':event.venue_name,
                'url':event.url

            });
        };

        res.contentType('application/json');
        res.write(JSON.stringify(result));
        res.end();
    });
    console.log(result);

})

var server = app.listen(81, function () {
    var host = server.address().address
    var port = server.address().port

    console.log("Example app listening at http://%s:%s", host, port)
})