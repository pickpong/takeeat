let menuCount = 0; // 초기 메뉴 카운트
let categoryCount = 0; // 초기 카테고리 카운트

document.addEventListener('click', function(e) {
    const target = e.target;

    // 메뉴 추가하기 버튼이 클릭된 경우
    if (target.classList.contains('add-menu-button')) {
        menuCount++;
        const menuContainer = target.closest('.category-container').querySelector('.menu-container');

        const menuHtml = `
        <div id="menu-${menuCount}">
            <ul class='no_dot'>
                <li>
                <button class="delete-menu-button del-button" data-menu-id="${menuCount}">메뉴 삭제</button>
                    <div class="line-container margin-top-20">
                        <div class="length-container">
                            <div class="s-info-text">메뉴를 입력하세요.</div>
                            <input type="text" id="marketMenu-${menuCount}" name="marketMenu" class="m-input-box margin-top-10"/>
                        </div>
                        <div class="length-container margin-left-10">
                            <div class="s-info-text">가격</div>
                            <input type="number" id="menuPrice-${menuCount}" name="menuPrice" class="s-input-box margin-top-10"/>
                        </div>
                    </div>
                    <div class="line-container margin-top-20">
                        <div class="length-container">
                            <div class="s-info-text">메뉴를 설명해주세요.</div>
                            <input type="text" id="menuIntro-${menuCount}" name="menuIntro" class="m-input-box margin-top-10"/>
                        </div>
                        <div class="length-container margin-left-10">
                            <div class="line-container">
                                <div class="s-info-text">최대주문수</div>
                                <div class="tip-container-center">
                                    <div class="tip">
                                        <p>주문 가능한 최대 수량을 제시해주세요.</p>
                                    </div>
                                </div>
                            </div>
                            <input type="number" id="maxCount-${menuCount}" name="maxCount" class="s-input-box margin-top-10"/>
                        </div>
                    </div>
                    <div class="s-info-text margin-top-10">메뉴 사진 등록</div>

                    <div class="line-container">
                        <img src="/images/no-image.jpg" class="img-style margin-top-15" id="img-preview-${menuCount}"/>
                        <label class="input-file-button" for="input-file-${menuCount}">이미지 업로드</label>
                        <input type="file" id="input-file-${menuCount}" class="file-style" onchange="previewImage(event, ${menuCount})" style="display:none">
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
                            <input type="text" id="menuCategory-${categoryCount}" name="menuCategory" class="l-input-box margin-top-10"/>
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
        if (!confirm ("아래 메뉴를 정말 삭제하시겠습니까?")) {
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

// 이미지 미리보기 함수
function previewImage(event, count) {
    const reader = new FileReader();
    reader.onload = function() {
        const output = document.getElementById(`img-preview-${count}`);
        output.src = reader.result;
    };
    reader.readAsDataURL(event.target.files[0]);
}