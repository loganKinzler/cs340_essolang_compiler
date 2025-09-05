let esolang_editor;
let trace_output;

$(function () {
    esolang_editor = document.getElementById("esolang-editor");
    trace_output = document.getElementById("trace-output");
});

function compile($) {
    fetch(contextPath + "/esolang_IDE", {
        method: "POST",
        headers: {"Content-Type": "plain/text"},
        body: esolang_editor.value
    
    }).then(response => {
        if (!response.ok) throw new Error("HTTP Error: ${response.status}");
        return response.text();

    }).then (body => {
        trace_output.innerHTML = body;

    }).catch(error => {
        console.log("Error: ", error);
    });
}

function run($) {

}