


var stack = {
    "DIV":{"id":"", "class":"", "style":"", "bgcolor":"", "align":"", "dir":"", "text":"", "lang":"", "vlink":"", "link":"", "alink":""}, 
    "U":{}, 
    "TABLE":{"width":"", "align":"", "cellspacing":"", "border":"", "cellpadding":"", "style":"", "bgcolor":"", "height":""}, 
    "TBODY":{"style":""}, 
    "TR":{"height":"", "style":"", "bgcolor":"", "valign":"", "width":"", "align":""}, 
    "TD":{"align":"", "width":"", "height":"", "colspan":"", "valign":"", "bgcolor":"", "background":"", "style":"", "rowspan":""}, 
    "A":{"href":"", "target":"", "style":"", "value":"", "rel":"", "name":""}, 
    "IMG":{"alt":"", "border":"", "style":"", "height":"", "src":"", "title":"", "width":"", "class":"", "tabindex":"", "align":"", "name":""}, 
    "H1":{"style":""}, 
    "P":{"style":"", "class":"", "align":""}, 
    "BR":{}, 
    "H2":{"style":""}, 
    "STRONG":{}, 
    "SPAN":{"style":"", "dir":"", "class":"", "data-term":"", "tabindex":"", "align":""}, 
    "FONT":{"color":"", "size":"", "face":""}, 
    "WBR":{}, 
    "B":{"class":""}, 
    "BLOCKQUOTE":{"class":"", "style":""}, 
    "EM":{}, 
    "H3":{"style":""}, 
    "UL":{"style":""}, 
    "LI":{}, 
    "PRE":{"style":""}, 
    "HR":{"style":""}, 
    "I":{}, 
    "FORM":{"name":"", "method":"", "action":"", "target":"", "onsubmit":""}, 
    "INPUT":{"type":"", "name":"", "value":""}
};

function findValidTags(node){
	if(typeof(node.tagName) === 'undefined'){
		return;
	}
	if(typeof(stack[node.tagName]) === 'undefined'){
		stack[node.tagName] = new Object();
		console.log('New Tag',node.tagName);
	}
	if(node.attributes){
		for (var a = 0;a<node.attributes.length;a++){
			if(typeof(stack[node.tagName][node.attributes[a].name]) === 'undefined'){
				console.log('New Attribute on Tag',node.tagName,node.attributes[a].name);
			}
			stack[node.tagName][node.attributes[a].name] = '';
			if(
				node.attributes[a].name.startsWith('aria')
				|| node.attributes[a].name.startsWith('data')
				|| node.attributes[a].name.startsWith('on')){  //detect likely failures to blacklist google's instrumentation of email content
				console.log(node);
			}
		}
	}
	if(node.childNodes){
		for (var a = 0;a<node.childNodes.length;a++){
			if(!node.childNodes[a].className || 
				(node.childNodes[a].className.indexOf('ajR') == -1//ajR seems to be the flag for the show/hide collapsed mail div in Google Mail.
					&& node.childNodes[a].className.indexOf('a6S') == -1//a6S seems to be the flag for download inline image button in Google Mail.
				)){
				findValidTags(node.childNodes[a]);
			}
		}
	}
}

var ele = document.getElementsByClassName('a3s');
for(var c=0;c<ele.length;c++){ 
	findValidTags(ele[c]);
}
JSON.stringify(stack);