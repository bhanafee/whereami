whereami
========

REST API to handle geolocation



API
===

<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>Method</th>
      <th>Request</th>
      <th>Response</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Get the area tags for a location</td>
      <td>GET</td>
      <td>/tags?latitude=38.0&longitude=122.0</td>
      <td>200 OK
<pre>{
    "location": {
        "latitude": 38.0,
        "longitude": 132.0
    }
    "distance": 1000.0,
}</pre>
      </td>
    </tr>
    <tr>
      <td>Get the distance from a point to the nearest area boundary</td>
      <td>GET</td>
      <td>/nearest?latitude=38.0&longitude=122.0</td>
      <td>200 OK
<pre>{
    "location": {
        "latitude": 38.0,
        "longitude": 132.0
    }
    "tags": [
        "CA",
        "San Francisco",
        "94123"
    ]
}</pre>
      </td>
    </tr>
    <tr>
      <td>Report a location and time, receiving a fence for the next report</td>
      <td>POST</td>
      <td>/track/:id
      <br/>
<pre>{
    "location": {
       "latitude": 38.0,
       "longitude": 122.0
    },
    "time": "2014-10-02T05:30:45.123Z"
}</pre>
      </td>
      <td>200 OK
<pre>{
    "circle": {
        "center": {
            "latitude": 38.0,
            "longitude": 122.0
        },
        "radius": 7400.0
    },
    "time": "2014-10-02T08:30:45.123Z"
}</pre></td>
    </tr>
    <tr>
      <td>Report a location, defaulting to the current time and receiving a fence for the next report</td>
      <td>GET</td>
      <td>/track/:id?latitude=38.0&longitude=122.0</td>
      <td>200 OK
<pre>{
    "circle": {
        "center": {
            "latitude": 38.0,
            "longitude": 122.0
        },
        "radius": 7400.0
    },
    "time": "2014-10-02T08:30:45.123Z"
}</pre></td>
    </tr>
    <tr>
      <td>Report that a device remains within a previously defined fence</td>
      <td>GET</td>
      <td>/track/:id/checkin</td>
      <td>200 OK</td>
    </tr>
  </tbody>
</table>

