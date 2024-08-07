


let menuCount = 0; // 초기 메뉴 카운트
let categoryCount = 0; // 초기 카테고리 카운트

window.saveMenu = function() {
    let categories = [];
    let menuImages = [];
    let hasIncompleteMenuData = false; // 필수 항목 누락 여부 확인 변수
    let hasEmptyMenu = false; // 빈 메뉴 여부 확인 변수
    let hasNoCategories = true; // 카테고리 입력 여부 확인 변수
    var formData = new FormData();
    // 모든 카테고리와 메뉴 정보 수집
    document.querySelectorAll('.category-container').forEach(categoryContainer => {
        let menus = [];

        const menuCategoryName = categoryContainer.querySelector('.menu-category').value;

        if (menuCategoryName) {
            hasNoCategories = false; // 카테고리가 입력되었음을 표시
        }

        // 현재 카테고리의 메뉴들 수집
        categoryContainer.querySelectorAll('.menu-item').forEach(menuItem => {
            const menuName = menuItem.querySelector('.m-input-box').value;
            const menuPrice = menuItem.querySelector('.s-input-box').value;
            const menuIntroduction = menuItem.querySelector('.menu-introduction').value;
            const menuImage = menuItem.querySelector('.file-style').files[0];



            // 메뉴 데이터가 모두 올바르게 수집되었는지 확인
            if (menuName && menuPrice) {
                let menuObject = {
                    menuName: menuName,
                    menuIntroduction: menuIntroduction,
                    menuPrice: parseInt(menuPrice)
                };
                menus.push(menuObject);
                formData.append('menuImages', menuImage);

                console.log("메뉴이미지:"+menuImages);
            } else {
                console.warn("메뉴 데이터가 완전하지 않습니다:", menuItem);
                hasIncompleteMenuData = true;
            }
        });
        // FormData 객체 생성




        // 현재 카테고리 정보를 categories 배열에 추가
        if (menuCategoryName && menus.length > 0) {
            categories.push({
                menuCategoryName: menuCategoryName,
                menus: menus
            });
        } else if ((menuCategoryName && menus.length === 0) || menuCategoryName === null) {
            console.warn("메뉴가 없습니다:", categoryContainer);
            hasEmptyMenu = true;
        } else {
            console.warn("카테고리가 없습니다:", categoryContainer);
            hasNoCategories = true;
        }
    });

    if (hasIncompleteMenuData) {
        alert("필수항목을 입력하세요.");
        return;
    }

    // 경고 메시지 표시
    if (hasEmptyMenu) {
        alert("메뉴를 입력하세요.");
        return;
    }

    if (categories.length === 0) {
        alert("하나 이상의 카테고리 또는 메뉴를 입력하세요.");
        return;
    }

    if (hasNoCategories) {
        alert("카테고리를 입력하세요.")
        return;
    }

    // 전체 데이터를 하나의 객체로 준비
    const data = {
        categories: categories
    };

    // 데이터 구조와 JSON 문자열 확인 (디버깅용)
    console.log("전송할 데이터:", JSON.stringify(data, null, 2));
    console.log("사진데이터:"+menuImages);

    function saveMenuImages(menuImages, successCallback, errorCallback) {


        $.ajax({
            url: '/market/menu/save/images', // 서버 측 파일 경로
            type: 'POST', // HTTP 요청 방식
            data: formData, // FormData 직접 전송
            processData: false, // 필수: 데이터 처리 방식 지정
            contentType: false, // 필수: 컨텐츠 타입 false로 설정
            success: function(response){
                if (typeof successCallback === 'function') {
                    successCallback(response);
                    window.location.href = '/market/option';
                }
            },
            error: function(xhr, status, error){
                if (typeof errorCallback === 'function') {
                    errorCallback(error);
                }
            }
        });
    }

    // 첫 번째 Ajax 요청 (Fetch API 사용)
    fetch('/market/menu/save', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        }
        throw new Error('Network response was not ok.');
    })
    .then(data => {
        alert('메뉴 저장 완료');
        console.log('Response data:', data);

        // 두 번째 Ajax 요청
        saveMenuImages([], function(response) {
            console.log('두 번째 Ajax 요청 성공:', response);
            // 추가적인 처리 가능
        }, function(error) {
            console.error('두 번째 Ajax 요청 실패:', error);
        });

        /*window.location.href = '/market/option';*/
    })
    .catch(error => {
        alert('저장 실패. 등록된 가게정보가 없습니다.');
        console.error('Error:', error);
    });
};



$(".input-file-button").on('change',function(){
  var fileName = $(".input-file-button").val();
  $(".file-style").val(fileName);
});

document.addEventListener('DOMContentLoaded', function() {
    const saveButton = document.getElementById('save-menu-button');
    if (saveButton) {
        saveButton.addEventListener('click', function(event) {
            event.preventDefault();
            window.saveMenu();
        });
    }

    document.addEventListener('click', function(e) {
        const target = e.target;

        // 메뉴 추가하기 버튼이 클릭된 경우
        if (target.classList.contains('add-menu-button')) {
            menuCount++;
            const menuContainer = target.closest('.category-container').querySelector('.menu-container');

            const menuHtml = `
            <div class="menu-item" id="menu-${menuCount}">
                <ul class='no_dot'>
                    <li>
                    <button class="delete-menu-button del-button" data-menu-id="${menuCount}">메뉴 삭제</button>
                        <div class="line-container margin-top-20">
                            <div class="length-container">
                                <div class="s-info-text essential">메뉴를 입력하세요.</div>
                                <input type="text" id="marketMenu-${menuCount}" th:field="*{marketMenu}" name="marketMenu" class="market-menu m-input-box margin-top-10"/>
                            </div>
                            <div class="length-container margin-left-10">
                                <div class="s-info-text essential">가격</div>
                                <input type="number" id="menuPrice-${menuCount}" th:field="*{menuPrice}" name="menuPrice" value="0" class="s-input-box margin-top-10"/>
                            </div>
                        </div>
                        <div class="s-info-text margin-top-10">메뉴를 설명해주세요.</div>
                        <input type="text" id="menuIntro-${menuCount}" th:field="*{menuIntro}" name="menuIntro" class="menu-introduction ll-input-box margin-top-10"/>
                        <div class="s-info-text margin-top-10 essential">메뉴 사진 등록</div>

                        <div class="line-container">
                            <img src="/images/no-image.jpg" class="img-style margin-top-15" id="img-preview-${menuCount}"/>
                            <label class="input-file-button" for="input-file-${menuCount}">이미지 업로드</label>
                            <input type="file" id="input-file-${menuCount}" th:field="*{menuImage}" name="menuImage" class="file-style" onchange="previewImage(event, ${menuCount})" style="display:none">
                        </div>
                        <hr class="hr-margin"/>
                    </li>
                </ul>
            </div>
            `;

            menuContainer.insertAdjacentHTML('beforeend', menuHtml);
        }
        // 메뉴 카테고리 추가하기 버튼이 클릭된 경우
        else if (target.id === 'add-category-button') {
            categoryCount++;
            const categoryContainer = document.getElementById('category-container');

            const categoryHtml = `
                <div class="category-container" id="category-${categoryCount}">
                    <ul class='no_dot'>
                        <li>
                            <button class="delete-category-button del-button" data-category-id="${categoryCount}">카테고리 삭제</button>
                            <div class="length-container margin-top-20">
                                <div class="info-text">메뉴 카테고리를 입력하세요.</div>
                                <input type="text" id="menuCategory-${categoryCount}" th:field="*{menuCategory}" name="menuCategory" class="menu-category l-input-box margin-top-10"/>
                            </div>
                            <div class="menu-container">
                                <!-- 여기에 해당 카테고리의 메뉴들이 추가될 부분 -->
                                <hr class="hr-margin"/>
                            </div>
                            <div class="s-line-text add-menu-button">메뉴 추가하기 +</div>
                        </li>
                    </ul>
                    <hr class="hr-margin-b"/>
                </div>
            `;

            categoryContainer.insertAdjacentHTML('beforeend', categoryHtml);
        }

        // 카테고리 삭제하기 버튼이 클릭된 경우
        else if (target.classList.contains('delete-category-button')) {
            const categoryId = target.getAttribute('data-category-id');
            deleteCategory(categoryId);
        }

        // 메뉴 삭제하기 버튼이 클릭된 경우
        else if (target.classList.contains('delete-menu-button')) {
            const menuId = target.getAttribute('data-menu-id');
            deleteMenu(menuId);
        }
    });

    function deleteMenu(menuId) {
        const menuElement = document.getElementById(`menu-${menuId}`);
        if (!confirm("아래 메뉴를 정말 삭제하시겠습니까?")) {
            event.preventDefault();
        } else {
            menuElement.remove();
        }
    }

    function deleteCategory(categoryId) {
        const categoryElement = document.getElementById(`category-${categoryId}`);
        if (!confirm("카테고리를 삭제하면 하위 메뉴가 삭제됩니다. 정말 삭제하시겠습니까?")) {
            event.preventDefault();
        } else {
            categoryElement.remove();
        }
    }
});

// 이미지 미리보기 함수
function previewImage(event, count) {
    const reader = new FileReader();
    reader.onload = function() {
        const output = document.getElementById(`img-preview-${count}`);
        output.src = reader.result;
    };
    reader.readAsDataURL(event.target.files[0]);
}