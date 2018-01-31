function writeBlockchain() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/chain",
        dataType: 'json',
        cache: false,
        timeout: 600000,
        success: function (chain) {
            var html = '<h3>MAIN CHAIN</h3>';
            html += '<table border="1"><tr><td>Height</td><td>PrevBlockHash</td><td>BlockHash</td></tr>';
            for (var i = 0; i < chain.length; i++) {
                html += '<tr><td>';
                html += i;
                html += '</td><td>';
                html += chain[i].prevHash;
                html += '</td><td>';
                html += chain[i].hash;
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

function writeBackupChains() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/backup",
        dataType: 'json',
        cache: false,
        timeout: 600000,
        success: function (chains) {
            var html = '<h3>BACKUP CHAINS</h3>';
            for (var i = 0; i < chains.length; i++) {
                html += '<h4>BACKUP CHAIN' + i + '</h4>'
                var chain = chains[i];
                html += '<table border="1"><tr><td>Height</td><td>PrevBlockHash</td><td>BlockHash</td></tr>';
                for (var j = 0; j < chain.length; j++) {
                    html += '<tr><td>';
                    html += j;
                    html += '</td><td>';
                    html += chain[j].prevHash;
                    html += '</td><td>';
                    html += chain[j].hash;
                    html += '</td></tr>'
                }
                html += '</table><br/>';
            }
            $('#body').append(html);
        },
        error: function (e) {
            console.log(e);
        }
    });
}