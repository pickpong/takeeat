document.getElementById('dropdownButton').addEventListener('click', function() {
    document.getElementById('dropdownContent').classList.toggle('show');
});

function selectCategory(category) {
    document.getElementById('dropdownButton').textContent = category;
    document.getElementById('dropdownContent').classList.remove('show');
}

window.onclick = function(event) {
    if (!event.target.matches('.dropdown-button')) {
        var dropdowns = document.getElementsByClassName('dropdown-content');
        for (var i = 0; i < dropdowns.length; i++) {
            var openDropdown = dropdowns[i];
            if (openDropdown.classList.contains('show')) {
                openDropdown.classList.remove('show');
            }
        }
    }
}
// 이미지 미리보기 함수
document.getElementById('input-file').addEventListener('change', function(event) {
    var input = event.target;
    var reader = new FileReader();

    reader.onload = function(){
        var dataURL = reader.result;
        var imgPreview = document.getElementById('img-preview');
        imgPreview.src = dataURL;
    };

    if (input.files && input.files[0]) {
        reader.readAsDataURL(input.files[0]);
    }
});