/*-------------------------------------------------------------------
    @팝업
		의견 및 평점남기기, 정보수정
-------------------------------------------------------------------*/
$(document).ready(function(){
	var scrollHeight = 0;//전역변수 초기화

	//팝업 띄우기
	function popupAdd(addName){
		$(addName).addClass('popup-active');
		$(addName).css('position','fixed');
		$(addName).css('display','block');
	}

	//팝업 닫기
	function popupRemove(removeName){
		$(removeName).removeClass('popup-active');
		$(removeName).css('position','static');
		$(removeName).css('display','none');
	}

	//'의견 및 평점남기기' 눌렀을 때
	$('.bots a').click(function(){
		var addName = '.popup-wrap';
		popupAdd(addName);
		$("body").addClass('layer-open'); //overflow:hidden 추가
	});

	//'내 의견 남기기' 눌렀을 때
	$('.btn-popup-next').click(function(){
		var addName = '.popup-wrap-2';
		var removeName = '.popup-wrap';
		popupAdd(addName);
		popupRemove(removeName);
	});

	//'정보수정' 눌렀을 때
	$('.info-update').click(function(){
		var addName = '.popup-wrap-3';
		popupAdd(addName);
		$("body").addClass('layer-open'); //overflow:hidden 추가
		
		$.ajax({
			url : '/user/selectUserById',
			contentType: 'application/json',
			type : 'POST',
			async : false,
			success : function (result, textStatus, xhr){
				console.log(xhr.status);
				console.log(result);
				
				var user = result.user;
				
				$('#user-name').val(user.name);
				$('#user-phone').val(user.phone);
				
			},
			error: function(jqXHR, textStatus, errorThrown){
			    console.log(textStatus + ' : ' + jqXHR.status + ' ' + errorThrown);
			}
		});
		
	});
	
	//'간편검색/정밀검색 설명 아이콘'
	$('.btn-tooltip').click(function(){
		var divLeft = event.pageX - 20;
		var divTop = event.pageY - 160;
		$('.popup-4').css({
			"top": divTop,
			"left": divLeft,
		}).show();
	});
	
	$('.btn-tooltip').click(function(){
		var addName = '.popup-wrap-4';
		var removeName = '.popup-wrap';
		popupAdd(addName);
		popupRemove(removeName);
	});
	
	//'원문보기'
	$('.btn-doc').click(function(){
		var addName = '.popup-wrap-5';
		popupAdd(addName);
		$("body").addClass('layer-open'); //overflow:hidden 추가
	});

	// '간편검색/정밀검색 설명' 외부영역 클릭 시 팝업 닫기
	$(document).mouseup(function(e){
		var removeName = '.popup-wrap-4';
		popupRemove(removeName);
		//$('.input-review').val('');
		$('.input-id-fix ~ *').val('');
		$("body").removeClass('layer-open'); //overflow:hidden 해제(스크롤 해제)
	});

	//'닫기(X)'버튼 눌렀을 때
	$('.btn-popup-close').click(function(){
		var removeName = '.popup-wrap, .popup-wrap-2, .popup-wrap-3, .popup-wrap-4, .popup-wrap-5';
		popupRemove(removeName);
		$('.input-review').val('');
		$('.input-id-fix ~ *').val('');
		$("body").removeClass('layer-open'); //overflow:hidden 해제(스크롤 해제)
		//$('#pdfboxDiv').empty();
	});

	//'취소'버튼 눌렀을 때
	$('.btn-popup-cancel').click(function(){
		var result = confirm('취소하시겠습니까? (저장하지않은 정보는 모두 삭제됩니다)');
		var removeName = '.popup-wrap-2, .popup-wrap-3';
		if(result) { //yes
			popupRemove(removeName);
			$("body").removeClass('layer-open'); //overflow:hidden 해제(스크롤 해제)*
			$('.input-review').val('');
			$('.input-id-fix ~ *').val('');
		} else { //no
			return;
		}
	});

	//'저장'버튼 눌렀을 때 (리뷰)
	$('.btn-popup-submit').click(function(){
		var result = confirm('저장하시겠습니까?');
		var removeName = '.popup-wrap-2';
		if(result) { //yes
		
			var ansSeq = $('#ans-seq').val();
			var qstContent = $('#qst-content').val();
			var qstOpinion = $('.input-review').val();
			var qstAnsRate = $('#popup2-starRev > span[class*="on"]').length;
			
			if(qstOpinion == ''){
				alert('의견을 입력해주세요.');
				return;
			}
			
			$.ajax({
				url : '/qna/insertQuestionOpinion',
				contentType: 'application/json',
				data : JSON.stringify({
					  'ansSeq' : parseInt(ansSeq),
					  'qstContent' : qstContent,
					  'qstOpinion' : qstOpinion,
					  'qstAnsRate' : qstAnsRate
				}),
				type : 'POST',
				async : false,
				success : function (result, textStatus, xhr){
					console.log(xhr.status);
					console.log(result);
					
					alert('저장되었습니다.');
					popupRemove(removeName);
					$("body").removeClass('layer-open'); //overflow:hidden 해제(스크롤 해제)*
					$('.input-review').val('');
				},
				error: function(jqXHR, textStatus, errorThrown){
				    console.log(textStatus + ' : ' + jqXHR.status + ' ' + errorThrown);
				}
			});
			
					
		} else { //no
			return;
		}
	});

	//'수정'버튼 눌렀을 떄 (정보수정)
	$('.btn-popup-update').click(function(){
		var result = confirm('수정하시겠습니까?');
		var removeName = '.popup-wrap-3';
		if(result) { //yes
			
			var password = $('#user-password').val();
			var newPassword = $('#new-password').val();
			var name = $('#user-name').val();
			var phone = $('#user-phone').val();
			
			if(phone.length > 11){
				alert('핸드폰 번호 길이가 깁니다.');
				$('#user-phone').val(phone.substring(0, 11));
				return;
			}
			
			if(password.length == 0){
				alert('비밀번호를 입력해주세요.');
				return;
			} else{
			}
			
			
			$.ajax({
				url : '/user/updateUser',
				contentType: 'application/json',
				data : JSON.stringify({
					'password' : password,
					'newPassword' : newPassword,
					'name' : name,
					'phone' : phone
				}),
				type : 'POST',
				async : false,
				success : function (result, textStatus, xhr){
					console.log(xhr.status);
					console.log(result);
					
					alert('수정되었습니다.');
					popupRemove(removeName);
					$("body").removeClass('layer-open'); //overflow:hidden 해제(스크롤 해제)*
					$('.input-id-fix ~ *').val('');
					
				},
				error: function(jqXHR, textStatus, errorThrown){
				    console.log(textStatus + ' : ' + jqXHR.status + ' ' + errorThrown);
				    
					if(jqXHR.status == 500){
						alert('비밀번호가 틀렸습니다.');
						return;
					}
				}
			});
		
		} else { //no
			return;
		}
	});

	//평점(별) 이벤트
	$('.starRev span').click(function(){
	  $(this).parent().children('span').removeClass('on');
	  $(this).addClass('on').prevAll('span').addClass('on');
	  return false;
	});
	$('#0.5').text();
});
