$(document).ready(function () {
    writeBlockchain();
});

function writeBlockchain() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/chain",
        dataType: 'json',
        cache: false,
        timeout: 600000,
        success: function (data) {
            var html = '<table border="1"><tr><td>Height</td><td>PrevBlockHash</td><td>BlockHash</td></tr>';
            for (var i = 0; i < data.length; i++) {
                html += '<tr><td>';
                html += i;
                html += '</td><td>';
                html += data[i].prevHash;
                html += '</td><td>';
                html += data[i].hash;
                html += '</td></tr>'
            }
            html += '</table>';
            $('#body').append(html);
        },
        error: function (e) {
            console.log(e);
        }
    });
}