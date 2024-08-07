IMP.init("imp58775808");
const socket = new SockJS('/connect/notification');
stompClient = Stomp.over(socket);

const paymentOrderRequest = {
    marketId: orderResponse.marketId,
    orderMenuRequests: []
};

// 각 메뉴에 대한 정보 추출
document.querySelectorAll('.menuContainer').forEach(menuContainer => {
    const menuId = parseInt(menuContainer.querySelector('.menu-id').getAttribute('data-menu-id'));
    // option-id 클래스를 가진 input 태그들에서 데이터 추출
    const optionIds = Array.from(menuContainer.querySelectorAll('.option-id')).map(option => parseInt(option.getAttribute('data-option-id')));

    const menuQuantity = parseInt(menuContainer.querySelector('.totalMenuPrice').getAttribute('data-menu-quantity'));


    // 주문 요청 객체에 추가
    paymentOrderRequest.orderMenuRequests.push({
        menuId: menuId,
        orderQuantity: menuQuantity,
        optionIds: optionIds
    });
});

$("#orderBtn").on("click", () => {
    IMP.request_pay({
        pg: "html5_inicis",
        pay_method: "card",
        merchant_uid: crypto.randomUUID().substring(0,8),   // 주문번호 8글자 생성
        name: $("#market-name").text(),
        amount: 100,  //테스트를 위한 금액
        //amount: totalPrice  <= 실제 금액
    }, function (rsp) { // callback

        if(rsp.success) {

            paymentOrderRequest.amount = totalPrice;
            paymentOrderRequest.orderCode = rsp.merchant_uid;
            paymentOrderRequest.requirement = $("#member-requirement").val();

            $.ajax({
                type: 'POST',
                url: "/payment/complete",
                contentType: 'application/json',
                data: JSON.stringify(paymentOrderRequest),
                success: function(response) {
                    //가게로 주문 전송
                    stompClient.send("/app/send-market/" + paymentOrderRequest.marketId, {}, JSON.stringify(response));
                    window.location.href = "/payment/result/" + response.orderId;
                },
                error: function(error) {
                    alert('결제에 문제가 발생했습니다! \n 결제가 취소가 이루어집니다.');
                    //@TODO 현재 페이지(주문하기)로 리다이렉트 진행 및 결제취소 로직 구현하기
                }
            }); //ajax end

        }else {
            //결제 실패 시 실행되는 로직
            var msg = '결제에 실패하였습니다.\n';
            msg += '사유 : ' + rsp.error_msg;
            alert(msg);
        }

    });
});