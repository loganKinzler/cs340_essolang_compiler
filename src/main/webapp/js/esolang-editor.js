let esolang_editor;

$(function () {
    esolang_editor = document.getElementById("esolang-editor");
});

function compile($) {
    const data = {
        action: "compile",
        test: "is works"
    };

    fetch(contextPath + "/esolang_IDE", {
        method: "POST",
        headers: {"Content-Type": "text/plain"},
        body: "test"
    
    }).then(response => {
        if (!response.ok) throw new Error("HTTP Error: ${response.status}");
        return response.json();
    
    }).then(data => {
        console.log(data);
    
    }).catch(error => {
        console.log("Error: ", error);
    });
}

function run($) {

}