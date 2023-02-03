
// Downalod functionality
$(document).ready(function () {
    $('#entity').change(function () {
        var selectedFileName = $(this).children("option:selected").val();
        var loc = "http://localhost:8080/api/download";
        var temp = "Download Entity Lookup Sample File";
        // document.getElementById("download").setAttribute("href", loc + "/" + selectedFileName);
        $.ajax({
            url: '/api/download/' + selectedFileName,
            type: 'GET',
            data: selectedFileName,
            success: function (returndata) {
                console.log(selectedFileName);

                $('select option[value="1"]').attr("selected", true);
                setInterval('location.reload()', 100);
            }
        })
    });
});
//successfully filled lookup file start


function submit() {
    document.getElementById("popupForm").style.display = "block";
}
function closeForm() {
    document.getElementById("popupForm").style.display = "none";
}

//successfully filled lookup file start
$(document).ready(function () {

    console.log(sessionStorage.getItem("status"));
    if (sessionStorage.getItem("status") == "true") {

        console.log("Primary popup");
        document.getElementById("popupForm").style.display = "block";
        sessionStorage.clear();
    }


})
function setStatus(s_status) {
    sessionStorage.setItem("status", s_status);
}


//select admin primary entity
$(document).ready(function () {
    $('#entitylist').change(function () {
        var selectedFileName = $(this).children("option:selected").val();
        $.ajax({
            url: '/api/add',
            type: 'POST',
            data: selectedFileName,
            success: function (returndata) {
                console.log(selectedFileName);
                setInterval('location.reload()', 100);
            }
        })
    })
});
$(document).ready(function () {
    $('#setPrimary').fadeOut(5000);
});
//selecting validate entity
$(document).ready(function () {
    $('#entityValidate').change(function () {
        var selectedFileName = $(this).children("option:selected").val();
        $.ajax({
            url: '/api/validate',
            type: 'POST',
            data: selectedFileName,
            success: function (returndata) {
                console.log(selectedFileName);
                setInterval('location.reload()', 100);
            }
        })
    })
});
//select validate Secondary entity
// $(document).ready(function () {
//     $('#entityValidateS').change(function () {
//         var selectedFileName = $(this).children("option:selected").val();
//         $.ajax({
//             url : '/api/validate',
//             type : 'POST',
//             data : selectedFileName,
//             success : function(returndata) {
//             console.log(selectedFileName);
//             }
//         })
//     })
// });
$(document).ready(function () {
    $('#mybtn-para').fadeOut(5000);
});
//validate global lookup
function validateForm() {
    var inputs = document.getElementsByTagName("input");
    for (var i = 0; i < inputs.length; i++) {
        if (inputs[i].type == "text") {
            var x = inputs[i].value;
            if (inputs[i].value == "" || inputs[0].value == "" || inputs[1].value == "" || inputs[2].value == "" || inputs[3].value == "") {
                //alert("Field should not be empty");
                swal({
                    //title: "Title",
                    text: "Field should not be empty",
                    timer: 3000,
                });
                // document.getElementById("issue").innerHTML = "Field should not be empty";
                return false;
            }
            else {
                return true;
            }
        }
    }
    //return true;
}
// $(document).ready(function () {
//     $('#add').click(function () {
//         var loc = document.getElementById("newdata");
//     loc.innerHTML = "<tr id='nw'><td><input type='text' id='1'/></td><td><input type='text' id='1'/></td></tr>";
//     });
// });
// deleteRow
function deleteRow(el, sform) {
    var selectedFileName = el.attributes.value.value;
    console.log(el);
    console.log(sform.id);
    console.log(selectedFileName);
    swal({
        title: "Are you sure?",
        icon: "warning",
        buttons: true,
        dangerMode: true,
    })
        .then((willDelete) => {
            if (willDelete) {
                swal("Your record has been deleted!", {
                    icon: "success",
                });
                var tbl = el.parentNode.parentNode.parentNode;
                var row = el.parentNode.parentNode.rowIndex;
                tbl.deleteRow(row);
                const formData = new FormData(sform);
                const formMap = new Map();
                for (var [key, value] of formData) {
                    formMap.set(key, value);
                }
                console.log(formMap);
                $.ajax({
                    url: '/api/writeSec/' + selectedFileName,
                    url: '/api/writeSec/' + selectedFileName,
                    type: 'POST',
                    data: Object.fromEntries(formMap),
                    dataType: "json",
                    success: function (formMap) {
                        console.log(formMap);
                        //setInterval('location.reload()',100);
                        //window.location = '/admin';
                    }
                })
            } else {
                swal("Your record is safe!");
            }
        });
    return false;
}
//add row
function addRow(id, i) {
    var t = document.getElementById(id);
    var rows = t.getElementsByTagName("tr");
    var r = rows[rows.length - 1];
    r.parentNode.insertBefore(getTemplateRow(i), r);
}
function getTemplateRow(i) {
    var x = document.getElementById(i).cloneNode(true);
    x.childNodes[0].nextSibling.firstChild.value = "";
    x.childNodes[0].nextSibling.firstChild.name = i;
    x.childNodes[0].nextElementSibling.nextElementSibling.firstChild.value = "";
    x.childNodes[0].nextElementSibling.nextElementSibling.firstChild.name = i + "val";
    console.log(x.childNodes);
    return x;
}

// fetching primary
$(document).ready(function () {
    $('#fetchbtn').click(function () {
        var selectedFileName = $(this).val();
        // const validatebtn = document.getElementById("validateBtn");
        $.ajax({
            url: '/api/validate/upload',
            type: 'POST',
            data: { 'selectedValueValidate': selectedFileName },
            success: function (returndata) {
                console.log(selectedFileName);
                $("#validateBtn").prop("hidden", false);
                $("#validateImg").prop("hidden", false);
                $('#fetchP').html('<b>' + selectedFileName + '</b>' + " fetched successfully and ready to validate");
            },
            error: function () {
                $('#fetchP').html('<b>' + selectedFileName + '</b>' + " not Found !");

            }
        })
    });
});

//validate primary
$(document).ready(function () {
    $('#validateBtn').click(function () {
        var selectedFileName = $(this).val();
        // const validatebtn = document.getElementById("validateBtn");
        $.ajax({
            url: '/api/validate/primary',
            type: 'POST',
            data: { 'entityValidate': selectedFileName },
            success: function (message) {
                console.log(message);
                let keys = Object.keys(message);
                let vals = Object.values(message);

                console.log(vals + ":" + keys);

                if (keys == "Error") {
                    console.log(keys + "=" + "Error");

                    $('#fetchP').html("");

                    $("#validateImg").prop("hidden", true);

                    document.getElementById("error").style.color = "red";

                    $('#error').html('<b>' + selectedFileName + '</b>' + " Validation Failed!");

                    $("#secondaryValidate").prop("disabled", true);
                } else {
                    $("#validateImg").prop("hidden", false);
                    $('#fetchP').html('<b>' + selectedFileName + '</b>' + " validated successfully");
                    $("#fetchbtn").prop("disabled", true);

                    document.getElementById("fetchbtn").style.border = "1px solid grey";
                    // $('#validate-record').html('<b>' + selectedFileName + '</b>' + " 0 error records found");
                    // $('#view-r').html('<i class="bi bi-eye"></i>View Report</a>');
                    // $('#download-r').html('<i class="bi bi-download"></i>Download Report</a>');

                    $('#report-details').append('<p id="record-no"></p>');
                    //$('#report-details').append('<a href="../view" onclick="showReport()" value=' + selectedFileName + ' id="primary-report-view"><i class="bi bi-eye"></i>View Report</a>');
                    $('#report-details').append('<a href="#" onclick="showReport(this.name)" class="mx-2" id="primary-report-view" name=' + selectedFileName+' ><i class="bi bi-eye">View Report</a>')
                    $('#report-details').append('<a href="" id="validate-download-primary"><i class="bi bi-download"></i>Download Report</a>');

                    $.ajax({
                        url: '/api/validateSelect',
                        type: 'GET',
                        data: { 'entityValidate': selectedFileName },
                        success: function (result) {
                            console.log(result);
                            $.each(result, function (key, value) {
                                $("#secondaryValidate").append('<option' + '>' + value + '</option>');
                                $("#FetchMore").append('<option' + '>' + value + '</option>');
                                console.log(value);
                            });


                        }
                    });

                    $("#secondaryValidate").prop("disabled", false);
                    document.getElementById("secondaryValidate").style.color = "#0D71AC";

                    document.getElementById("secondaryValidate").style.border = "1px solid #0D71AC";
                    document.getElementById("secondaryProcessMessage").style.color = "black";
                }
                //call secondary

                // $.ajax({
                //     url: '/api/validateSelect',
                //     type: 'GET',
                //     data: { 'entityValidate': selectedFileName },
                //     success: function (result) {
                //         console.log(result);
                //         $.each(result, function (key, value) {
                //             $("#secondaryValidate").append('<option' + '>' + value + '</option>');
                //             console.log(value);
                //         });


                //     }
                // })

                //end call secondary
                $.ajax({
                    url: '/api/validate/messageprimary',
                    type: 'GET',
                    data: { 'entityValidate': selectedFileName },
                    success: function (result) {
                        console.log(result[result.length-1]);
                        
                            $('#messageOutput').html(result[result.length-2]);
                            if(result[result.length-1]>0){
                                $('#record-no').html(result[result.length-1]+ " error records found!");

                            }else{
                                $('#record-no').html("0 error records found!");
                            }
                        


                    }
                })

                $("#validateBtn").prop("hidden", true);
                //$("#validateImg").prop("hidden", false);

                document.getElementById("fetchP").style.color = "green";
                // $('#fetchP').html('<b>' + selectedFileName + '</b>' + " validated successfully");
                // $("#secondaryValidate").prop("disabled", false);
                // document.getElementById("secondaryValidate").style.color = "#0D71AC";
                // document.getElementById("secondaryProcessMessage").style.color = "black";

            }
        })

    });
});

//Transform
$(document).ready(function () {
    $('#transformBtn').click(function () {
        var selectedFileName = $(this).val();
        console.log(selectedFileName);
        // const validatebtn = document.getElementById("validateBtn");
        $.ajax({
            url: '/api/transforming',
            type: 'POST',
            data: { 'entityTransform': selectedFileName },
            success: function (returndata) {
                console.log(selectedFileName);
                $("#transformBtn").prop("hidden", true);
                // document.getElementById("transformBtn").style.visibility = "hidden";
                // $("#transformMessage").prop("hidden", true);
                document.getElementById("transformMessage").style.visibility = "hidden";
                // $('#fetchP').html('<b>'+selectedFileName+'</b>'+" fetched successfully and ready to validate");
                $("#myProgress").prop("hidden", false);

                move();
            }
        })
    });
});

//Select button for secondary entity list selection
$(document).ready(function () {
    $('#secondaryValidate').change(function () {
        var selectedFileName = $(this).children("option:selected").val();
        var selectFolderName = document.getElementById("validateBtn").value;
        console.log(selectFolderName);
        $.ajax({
            url: '/api/validate/secondary',
            type: 'POST',
            data: { 'entityValidate': selectedFileName, 'primaryEntityValidate': selectFolderName },
            success: function (returndata) {
                console.log(selectedFileName);
                $('#secondaryFetchImg').prop("hidden", false);
                document.getElementById("fetchS").style.color = "black";
                $('#fetchS').html('<b>' + selectedFileName + '</b>' + " fetched successfully and ready to validate");
                $('#secondaryValidateBtn').prop("hidden", false);
            }
        })
    });
});


//validate secondary
$(document).ready(function () {
    $('#secondaryValidateBtn').click(function () {
        var selectedFileName = document.getElementById("secondaryValidate").value;
        var selectFolderName = document.getElementById("validateBtn").value;
        // const validatebtn = document.getElementById("validateBtn");
        $.ajax({
            url: '/api/validate/validateSecondary',
            type: 'GET',
            data: { 'entityValidate': selectedFileName, 'primaryEntityValidate': selectFolderName },
            success: function (message) {
                console.log(message);
                let keys = Object.keys(message);
                let vals = Object.values(message);

                console.log(vals + ":" + keys);

                if (keys["Error"]!= null) {
                    console.log(keys + "=" + "Error");

                    $('#fetchS').html("");

                    $("#secondaryFetchImg").prop("hidden", true);

                    document.getElementById("errorS").style.color = "red";

                    $('#errorS').html('<b>' + selectedFileName + '</b>' + " Validation Failed!");

                    $("#secondaryValidate").prop("disabled", true);
                } else {
                document.getElementById("fetchS").style.color = "green";
                $('#fetchS').html('<b>' + selectedFileName + '</b>' + " validated successfully");
                $('#secondaryValidateBtn').prop("hidden", true);
                $('#secondaryValidate').prop("hidden", false);
                $('#view-reports').prop("hidden", false);

                    document.getElementById("fetchbtn").style.border = "1px solid grey";
                    // $('#validate-record').html('<b>' + selectedFileName + '</b>' + " 0 error records found");
                    // $('#view-r').html('<i class="bi bi-eye"></i>View Report</a>');
                    // $('#download-r').html('<i class="bi bi-download"></i>Download Report</a>');

                    $('#sreport-details').append('<p id="srecord-no"></p>');
                    //$('#report-details').append('<a href="../view" onclick="showReport()" value=' + selectedFileName + ' id="primary-report-view"><i class="bi bi-eye"></i>View Report</a>');
                    $('#sreport-details').append('<a href="#" onclick="showReport(this.name)" id="secondary-report-view" name=' +selectFolderName+"/"+ selectedFileName+'><i class="bi bi-eye"></i>View Report</a>')
                    $('#sreport-details').append('<a href="" id="validate-download-secondary"><i class="bi bi-download"></i>Download Report</a>');
                    if(keys["Count"]>0){
                        $('#srecord-no').html(keys["Count"]+ " error records found!");

                    }else{
                        $('#srecord-no').html("0 error records found!");
                    }

                
                //var tr = document.createElement("tr");
                tr = `<tr>`;
                var td = document.createElement("td");
                const i = document.createElement('i');
                const j = document.createElement('i');
                i.className = "bi bi-eye";
                j.className = "bi bi-download";
                // li.appendChild(i);
                // li.appendChild(document.createTextNode(selectedFileName));

                // li.appendChild(j);
                // li.appendChild(document.createTextNode("Download Report"));
                // ul.appendChild(li);
                // ul.insertBefore(i,li);
                tr += `<td><i class="bi bi-eye"></i>` + ` ` + selectedFileName + `</td>`;
                tr += `<td><i class="bi bi-download"></i>` + ` ` + "Download Report" + `</td>`;
                tr += `</tr>`;
                $('table').append(tr);
                //document.getElementById("add-reports").innerHTML= tr;
                $('#validateDone').prop("hidden", false);
                }

            }
        })
    });
});



// Function for progress bar

function move() {
    var i = 0;
    if (i == 0) {
        i = 1;
        var elem = document.getElementById("myBar");
        console.log(elem);
        var width = 1;
        var id = setInterval(frame, 10);
        function frame() {
            if (width >= 100) {
                clearInterval(id);
                i = 0;
            } else {
                width++;
                elem.style.width = width + "%";
                elem.innerHTML = width + "%";
                if (width == 100) {

                    $('#transformMessage').html("Completed!");
                    document.getElementById("transformMessage").style.visibility = "visible";
                    $('#transformComplete').html(" Transformation completed successfully");
                    $('#transformDone').prop("hidden", false);

                }
                else {
                    $('#transformComplete').html(" Transforming...");
                }
            }
        }
    }


}

//bg colour chooser for cards
// $(document).ready(function () {
//     var index = 0;
//     $(".small_circle").each(function (item) {
//         var colors = ["#D8C595 ", "#3E64B8", "#56BDC5", "#656464", "#6EC3E1", "#2D6664", "#F0823D", "#2B8CC6"];
//         var colorsLength = colors.length;
//         var colorIndex = index % colorsLength;
//         $(this).css("background-color", colors[colorIndex]);
//         index++;
//     });
// });

// Global Lookup Popup Script
function openForm() {
    document.getElementById("popupForm").style.display = "block";
}
function closeForm() {
    document.getElementById("popupForm").style.display = "none";
}

//For Secondary Lookup submit
$(document).ready(function () {


    $('#submitSec').click(function () {
        var selectedFileName = $(this).val();
        console.log(selectedFileName);
        console.log(selectedFileName);
        // const validatebtn = document.getElementById("validateBtn");
        //var formsCollection = document.getElementsByTagName("form");

        var f = []


        const formm = new Map();
        var dataModel = {};

        for (var i = 0; i < document.forms.length; i++) {
            var foo = document.forms[i];
            var foo = document.forms[i];
            var fd = new FormData(foo);
            console.log(fd);
            const formMap = new Map();
            for (var [key, value] of fd) {

                formMap.set(key, value);

            }
            console.log(formMap);
            $.ajax({
                url: '/api/writeSec/' + selectedFileName,
                type: 'POST',
                dataType: "json",
                //contentType: "application/json",
                data: Object.fromEntries(formMap),
                success: function (message) {
                    console.log(message);
                }
            })


        };

    })
});

$(document).ready(function () {
    var i = $('#boolean').attr("name");
    console.log(i);
    if (i == "false") {
        console.log(i);
        $('#hr').prop("hidden", true);
        $('#secondary-circle').prop("hidden", true);
        $('#submit').prop("hidden", true);
        document.getElementById("popup-close").setAttribute("href", "../main");

    }
});

//search functionality
$(function () {
    // Document is ready    
    $("#searchQueryInputLookup").keyup(function () {
        var value = $("#searchQueryInputLookup").val();
        var loc = "../api/primary/";
        var result = document.querySelector('.output');
        var element = document.getElementById("searchLink");
        // element.style.visibility = "visible";       
        if (!value) {
            result.innerHTML = '';
            return
        }

        $.ajax({
            type: "GET",
            url: "/api/search",
            data: { 'name': value },
            dataType: "JSON",
            success: function (data) {
                //Receiving the result of search here                
                var res = '<ul>';
                data.forEach(e => {
                    // res += '<li id="autocomplete">' + e + '</li>';                    
                    res += `<li id="autocomplete"` + 'value="' + e + '"' + `>` + `<a id="test" href="` + loc + e + `"` + `>` + e + `</a>` + `</li>`;
                })
                res += '</ul>';
                result.innerHTML = res;
            }
        });
    });
});
$(function () {
    // Document is ready    
    $("#searchQueryInputValidate").keyup(function () {
        var value = $("#searchQueryInputValidate").val();
        var loc = "../api/entityValidate/";
        var result = document.querySelector('.output');
        var element = document.getElementById("searchLink");
        // element.style.visibility = "visible";       
        if (!value) {
            result.innerHTML = '';
            return
        }

        $.ajax({
            type: "GET",
            url: "/api/search",
            data: { 'name': value },
            dataType: "JSON",
            success: function (data) {
                //Receiving the result of search here                
                var res = '<ul>';
                data.forEach(e => {
                    // res += '<li id="autocomplete">' + e + '</li>';                    
                    res += `<li id="autocomplete"` + 'value="' + e + '"' + `>` + `<a id="test" href="` + loc + e + `"` + `>` + e + `</a>` + `</li>`;
                })
                res += '</ul>';
                result.innerHTML = res;
            }
        });
    });
});
$(function () {
    // Document is ready    
    $("#searchQueryInputTransform").keyup(function () {
        var value = $("#searchQueryInputTransform").val();
        var loc = "../api/transforming/";

        var result = document.querySelector('.output');
        var element = document.getElementById("searchLink");
        // element.style.visibility = "visible";       
        if (!value) {
            result.innerHTML = '';
            return
        }

        $.ajax({
            type: "GET",
            url: "/api/search",
            data: { 'name': value },
            dataType: "JSON",
            success: function (data) {
                //Receiving the result of search here                
                var res = '<ul>';
                data.forEach(e => {
                    // res += '<li id="autocomplete">' + e + '</li>';                    
                    res += `<li id="autocomplete"` + 'value="' + e + '"' + `>` + `<a id="test" href="` + loc + e + `"` + `>` + e + `</a>` + `</li>`;
                })
                res += '</ul>';
                result.innerHTML = res;
            }
        });
    });
});

//For report
function showReport(value) {
    let table = document.getElementById("reportTable");
    let ta = document.getElementById("reportTable1");
   // var value = document.getElementById("primary-report-view").value;
    console.log(value);
    
    $.ajax({
        type: "GET",
        url: "/api/view-reports",
        data: { 'name': value },
        success: function (name) {
            console.log("HHHH");
            console.log(name[0]);
            table_data = `<table style="width:auto;">`;
            if (name[0] != null) {
                for (const line in name[0]) {
                    var list = name[0][line].split(",");
                    if (line == 0) {
                        table_data += `<tr>`;
                        for (var i = 0; i < list.length; i++) {
                            table_data += `<td colspan=2><b>` + list[i] + `</b></td>`;
                        }

                        table_data += `</tr>`;
                    } else {

                        table_data += `<tr>`;
                        for (var i = 0; i < list.length; i++) {
                            table_data += `<td class="report-col px-2 py-1">` + list[i] + `</td>`;
                        }
                        table_data += `</tr>`;
                    }

                }
                table_data += `</table>`;
                table.innerHTML = table_data;
                
            }
            t_data = `<table style="width:auto;">`;
            if (name[1] != null) {
                for (const line in name[1]) {
                    var list = name[1][line].split(",");
                    if (line == 0) {
                        t_data += `<tr>`;
                        for (var i = 0; i < list.length; i++) {
                            t_data += `<td class="px-2 py-1"><b>` + list[i] + `</b></td>`;
                        }

                        t_data += `</tr>`;
                    } else {

                        t_data += `<tr>`;
                        for (var i = 0; i < list.length; i++) {
                            t_data += `<td class="report-col px-2 py-1">` + list[i] + `</td>`;
                        }
                        t_data += `</tr>`;
                    }

                }
                t_data += `</table>`;
                ta.innerHTML =  t_data;
            }
             document.getElementById('id01').style.display='block';


        }
    });
};

function loadProcess(){
    var loadEntity =$('#load-btn').val();
    console.log(loadEntity);
    $.ajax({
        type: "GET",
        url: "/api/load/process",
        data: { 'loadEntity': loadEntity },
        dataType: "JSON",
        success: function (data) {
            console.log(data);
        }
    });
}

