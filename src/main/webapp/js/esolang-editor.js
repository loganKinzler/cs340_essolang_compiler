let esolang_editor;
let trace_output;
let graphics_output;
// let animation_output;

$(function () {
    esolang_editor = document.getElementById("esolang-editor");
    trace_output = document.getElementById("trace-output");
    graphics_output = document.getElementById("graphics-output");
    // animation_output = document.getElementById("graphics-animation");
});

function compile($) {
    fetch(contextPath + "/compile", {
        method: "POST",
        headers: {"Content-Type": "plain/text"},
        body: esolang_editor.value
    
    }).then(response => {
        if (!response.ok) throw new Error("HTTP Error: ${response.status}");
        return response.text();

    }).then (body => {
        trace_output.innerHTML = "Compiling...\n" + body + "Compiled!\n\n";

    }).catch(error => {
        console.log("Error: ", error);
    });
}

function run($) {
    fetch(contextPath + "/run", {
        method: "POST",
        headers: {"Content-Type": "plain/text"}

    }).then(response => {
        if (!response.ok) throw new Error("HTTP Error: ${response.status}");
        return response.json();

    }).then (json => {
        const htmlJSON = json.HTML;
        const animationJSON = json.Animation;

        trace_output.innerHTML += "Running...\n";

        graphics_output.innerHTML = htmlJSON;
        // animation_output.innerHTML = animationJSON;

        applyAnimation();

    }).catch(error => {
        console.log("Error: ", error);
    });
}

function applyAnimation() {
    for (let i=0; i<graphics_output.children.length; i++)
        graphics_output.children[i].style.animation= "rotate 4s linear infinite";
}