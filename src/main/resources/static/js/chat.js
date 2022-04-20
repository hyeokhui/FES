/**
 * 
 */

/*-------------------------------------------------------------------
    @'답변문서' 카테고리 탭
-------------------------------------------------------------------*/
$(document).ready(function(){

	$('#qstText').focus();
	$("#qstText").bind("keydown", function(e) {
		if (e.keyCode == 13) { // enter key
			$('#chat-action').trigger('click');
			return false;
		}
	});

	
	// @'답변문서' 카테고리 탭
	$('#chat-action').click(function(){
		
		var ansTypeText = $('#ansType option:selected').text();
		var ansTypeVal = $('#ansType option:selected').val();
		var searchTypeVal = $('input[name=searchType]:checked').val();
		var searchTypeText = $('label[for="' + searchTypeVal + '"]').text();
		var ansYearText = '';
		var ansYearStartVal = $('#ansYearStart option:selected').val();
		var ansYearEndVal = $('#ansYearEnd option:selected').val();
		var ansYearStartText = $('#ansYearStart option:selected').val() == 'all'? '' : $('#ansYearStart option:selected').text();
		var ansYearEndText = $('#ansYearEnd option:selected').val() == 'all'? '' : $('#ansYearEnd option:selected').text();
		var qstText = $('#qstText').val();
		
		if(qstText == ''){
			alert('질문을 입력해주세요.');
			return;
		}
		
		if(ansYearStartVal == 'all' && ansYearEndVal == 'all'){
			ansYearText ='전체';
		} else if(ansYearStartVal != 'all' && ansYearEndVal != 'all'){
			if(ansYearStartVal > ansYearEndVal){
				alert('시작연도가 종료연도보다 클 수 없습니다.');
				return;
			} else{
				ansYearText = ansYearStartText + '~' + ansYearEndText;
			}
		} else{
			ansYearText = ansYearStartText + '~' + ansYearEndText;
		}
		
		// 질문 생성
		qstIdx++;
		var qstHtml = '';
		
		qstHtml += '<li class="right">';
		qstHtml += 	'<div class="user">';
		qstHtml += 		'<p>[답변유형 : ' + ansTypeText +' / 검색유형 : ' + searchTypeText + ']<br>';
		qstHtml += '<span id="qst-text-' + qstIdx + '">' + qstText + '</span>';
	    qstHtml += 		'</p>';
	    qstHtml += 	'</div>';
	    qstHtml += '</li>';

		$('.chat > ul').append(qstHtml);
		
		// 로딩 이미지 show
		$("#div_load_image").show();
		
		// 답변 ajax 통신 후 답변 리턴
		var qstFormAction = $('#qstForm').attr('action');
		
		$.ajax({
			url : qstFormAction,
			contentType: 'application/json',
			data : JSON.stringify({
				  'ansType' : ansTypeVal
				, 'searchType' : searchTypeVal
				, 'ansYearStart' : ansYearStartVal
				, 'ansYearEnd' : ansYearEndVal
				, 'question' : qstText
			}),
			type : 'POST',
			//async : false,
			success : function (data, textStatus, xhr){
				//console.log(xhr.status);
				//console.log(data);
				
				$("#div_load_image").hide();
				$('#qstText').val('');
				
				var ansHtml = '';
				
				if(data.mrcResultMapList.length == 0){
					ansHtml += '<li class="left">';
					ansHtml += 	'<div class="bots">';
					ansHtml += 		'<p>알맞은 답변을 찾지 못했습니다.</p>';
				    ansHtml += 	'</div>';
				    ansHtml += '</li>';
			
					$('.chat > ul').append(ansHtml);
					
					return;
				}
				
				var resultData = data.mrcResultMapList[0];
				
				ansParamMap.push(data.paramMap);
				//ansMrcResult.push(data.mrcResultMapList);
				ansMrcResult[ansIdx] = data.mrcResultMapList;
				//ansMrcParagraphSources.push(data.mrcParagraphSources);
				ansMrcParagraphSources[ansIdx] = data.mrcParagraphSources;
				//ansDocResultSources.push(data.aiadDocResultSources);
				ansDocResultSources[ansIdx] = data.aiadDocResultSources;
				
				var paragraphSourcesObj = [];
				paragraphSourcesObj.push(data.pdfParagraphSources);
				paragraphSourcesObj.push(data.articleParagraphSources);
				paragraphSourcesObj.push(data.guideParagraphSources);
				
				ansParagraphSources.push(paragraphSourcesObj);
				
				if(ansTypeVal == 'all'){
					var searchInfo = {};
					
					searchInfo.ansType = 'pdf';
					searchInfo.searchCnt = [
						{'type' : 'pdf', 'cnt' : 1},
						{'type' : 'article', 'cnt' : 0},
						{'type' : 'guide', 'cnt' : 0}
					];
					searchInfo.searchIdx = [
						{'type' : 'pdf', 'fromIdx' : 0, 'toIdx' : 30},
						{'type' : 'article', 'fromIdx' : 0, 'toIdx' : 30},
						{'type' : 'guide', 'fromIdx' : 0, 'toIdx' : 30}
					];
					
					ansSearchInfo.push(searchInfo);
					
				} else if(ansTypeVal == 'pdf'){
					var searchInfo = {};
					
					searchInfo.ansType = 'pdf';
					searchInfo.searchCnt = [
						{'type' : 'pdf', 'cnt' : 1}
					];
					searchInfo.searchIdx = [
						{'type' : 'pdf', 'fromIdx' : 0, 'toIdx' : 25}
					];
					
					ansSearchInfo.push(searchInfo);
					
				} else if(ansTypeVal == 'article'){
					var searchInfo = {};
					
					searchInfo.ansType = 'article';
					searchInfo.searchCnt = [
						{'type' : 'article', 'cnt' : 0}
					];
					searchInfo.searchIdx = [
						{'type' : 'article', 'fromIdx' : 0, 'toIdx' : 25}
					];
					
					ansSearchInfo.push(searchInfo);
					
				} else if(ansTypeVal == 'guide'){
					var searchInfo = {};
					
					searchInfo.ansType = 'guide';
					searchInfo.searchCnt = [
						{'type' : 'guide', 'cnt' : 0}
					];
					searchInfo.searchIdx = [
						{'type' : 'guide', 'fromIdx' : 0, 'toIdx' : 25}
					];
					
					ansSearchInfo.push(searchInfo);
				}
				
				//var ansDocReport = [];
				var ansDocResearch = [];	// 백서 pdf
				var ansDocArticle = [];		// 기사
				var ansDocGuide = [];		// 공지
				var ansDoc = [];
				
				var distAiadDocResultSources = [];
				distAiadDocResultSources = data.aiadDocResultSources.reduce(function(acc, current) {
					if (acc.findIndex(({ doc_id_STR }) => doc_id_STR === current.doc_id_STR) === -1) {
					  acc.push(current);
					}
				  	return acc;
				}, []);
				
				distAnsDocResultSources.push(distAiadDocResultSources);
				
				var resultArr = [];
				//ansMrcResult[ansIdx] = [];
				ansMrcResult[ansIdx].map(item => {
				    //for each item in arrayOfObjects check if the object exists in the resulting array
				    if(resultArr.find(object => {
				        if(object.docId === item.docId && object.sentenceIndex === item.sentenceIndex && object.pageNumber === item.pageNumber) {
				            return true;
				        } else {
				            return false;
				        }
				    })){
				    } else {
				        resultArr.push(item);
				    }
				});
				ansMrcResult[ansIdx] = [];
				ansMrcResult[ansIdx].push(...resultArr);
				var resultArr = [];
				
				//ansMrcParagraphSources[ansIdx] = [];
				ansMrcParagraphSources[ansIdx].map(item => {
				    //for each item in arrayOfObjects check if the object exists in the resulting array
				    if(resultArr.find(object => {
				        if(object.doc_id_STR === item.doc_id_STR && object.paragraph_id_STR === item.paragraph_id_STR && object.paragraph_number_INT === item.paragraph_number_INT) {
				            return true;
				        } else {
				            return false;
				        }
				    })){
				    } else {
				        resultArr.push(item);
				    }
				});
				ansMrcParagraphSources[ansIdx] = [];
				ansMrcParagraphSources[ansIdx].push(...resultArr);
				var resultArr = [];
				
				resultArr = ansDocResultSources[ansIdx].reduce(function(acc, current) {
					if (acc.findIndex(({ doc_id_STR }) => doc_id_STR === current.doc_id_STR) === -1) {
					  acc.push(current);
					}
				  	return acc;
				}, []);
				ansDocResultSources[ansIdx] = [];
				ansDocResultSources[ansIdx].push(...resultArr);
				
				for(var i=0; i<ansDocResultSources[ansIdx].length; i++){
					var mrcScore = ansDocResultSources[ansIdx][i].mrcScore;
					var docTitleStr = ansDocResultSources[ansIdx][i].doc_title_STR;
					var docTypeStr = ansDocResultSources[ansIdx][i].doc_type_STR;
					var docArticleNameStr = ansDocResultSources[ansIdx][i].doc_article_name_STR;
					//var source = ansDocResultSources[0][i];
					//var docCategory = source.doc_type_STR;
					var channelNameStr = ansDocResultSources[ansIdx][i].channel_name_STR;
					
					if(channelNameStr == '파일 업로드' ||
						channelNameStr == '농림축산검역본부 도서관'){
						
						data.aiadDocResultSources[i].doc_type = '백서';
						ansDocResultSources[ansIdx][i].doc_type = '백서';
						
						var ansDocObj = {};
						ansDocObj.mrcScore = mrcScore;
						ansDocObj.docTitleStr = docTitleStr;
						ansDocObj.docTypeStr = '백서';
						ansDocObj.docArticleNameStr = docArticleNameStr;
						ansDocObj.docIdx = i;
						
						ansDocResearch.push(ansDocObj);
						
					} else if(channelNameStr == '양돈타임즈 칼럼' ||
								channelNameStr == '양돈타임즈 현장' ||
								channelNameStr == '양돈타임즈 해외양돈' ||
								channelNameStr == '양돈타임즈 오늘의 뉴스' ||
								channelNameStr == '돼지와사람 전체 기사' ||
								channelNameStr == '데일리벳 뉴스' ||
								channelNameStr == '농민신문 최신 기사' ||
								channelNameStr == '축산신문 전체 기사' ||
								channelNameStr == 'ProMed-Mail'){
						
						data.aiadDocResultSources[i].doc_type = '뉴스';
						ansDocResultSources[ansIdx][i].doc_type = '뉴스';
						
						var ansDocObj = {};
						ansDocObj.mrcScore = mrcScore;
						ansDocObj.docTitleStr = docTitleStr;
						ansDocObj.docTypeStr = '뉴스';
						ansDocObj.docArticleNameStr = docArticleNameStr;
						ansDocObj.docIdx = i;
						
						ansDocArticle.push(ansDocObj);
						
					} else if(channelNameStr == '농림축산식품부 알림소식 보도자료' ||
								channelNameStr == '농림축산검역본부 알림마당-보도/해명자료'){
						
						data.aiadDocResultSources[i].doc_type = '기관 공고';
						ansDocResultSources[ansIdx][i].doc_type = '기관 공고';
						
						var ansDocObj = {};
						ansDocObj.mrcScore = mrcScore;
						ansDocObj.docTitleStr = docTitleStr;
						ansDocObj.docTypeStr = '기관 공고';
						ansDocObj.docArticleNameStr = docArticleNameStr;
						ansDocObj.docIdx = i;
						
						ansDocGuide.push(ansDocObj);
						
					}
				}
				
				//ansDoc.push(ansDocReport);
				ansDoc.push(ansDocResearch);
				ansDoc.push(ansDocArticle);
				ansDoc.push(ansDocGuide);
				
				ansDocDiv.push(ansDoc);
				
				// 답변 insert
				var ansSeq;
				
				$.ajax({
					url : '/qna/insertAnswer',
					contentType: 'application/json',
					data : JSON.stringify({
						   'ansContent' : resultData.answer
						 , 'docId' : resultData.docId
						 , 'paragraphId' : data.mrcParagraphSources[0].paragraph_id_STR
					}),
					type : 'POST',
					async : false,
					success : function (result, textStatus, xhr){
						//console.log(xhr.status);
						//console.log(result);
						
						ansSeq = result.ansSeq;
					},
					error: function(jqXHR, textStatus, errorThrown){
					    console.log(textStatus + ' : ' + jqXHR.status + ' ' + errorThrown);
					}
				});
				
				
				// 채팅 답변
				
				ansHtml += '<li class="left">';
				ansHtml += 	'<div class="bots" data-ans-idx=' + ansIdx + '>';
				ansHtml += 		'<p>' + resultData.answer + '<span> (SCORE : ' +  resultData.probability + ')</span></p>';
				ansHtml +=		'<h3 class="mt-5">※ SCORE가 낮다면, 계속해서 검색해보세요.</h3>';
				ansHtml +=		'<h3>정확한 답변의 평균 SCORE는 90 전후 입니다.</h3>';
				ansHtml += 		'<button type="button" class="chat-doc" onclick="layout(this)" data-ans-idx=' + ansIdx + '>관련문서보기</button>';
				ansHtml += 		'<a data-ans-seq=' + ansSeq + '>★ 의견 및 평점 남기기</a>';
			    ansHtml += 	'</div>';
			    ansHtml += '</li>';
		
				$('.chat > ul').append(ansHtml);
				
				ansIdx++;
				
				// 답변 의견/평점 클릭 팝업 이벤트
				var ans = $('.bots a');
				var ansIndex = ans.length-1;
				
				$('.bots a').eq(ansIndex).click(function(){
					var idx = $(this).data('ans-seq');
					var addName = '.popup-wrap';
					popupAdd(addName);
					
					// popup 내용 생성
					$('#ans-seq').val(idx);
					$('#qst-content').val($('#qst-text-' + qstIdx).text());
					setPopupAnsRate(idx);
					
					$("body").addClass('layer-open'); //overflow:hidden 추가
				});
				
				// 채팅 입력시 스크롤 최하단
				$('.chat-content').scrollTop($('.chat-content')[0].scrollHeight);
				//$('#resultDiv').html('');
				//location.href = "/";
			},
			error: function(jqXHR, textStatus, errorThrown){
			    console.log(textStatus + ' : ' + jqXHR.status + ' ' + errorThrown);
			    
				$("#div_load_image").hide();
			}
		});
		
		
		
		
		
	});
	
	//팝업 띄우기
	function popupAdd(addName){
		$(addName).addClass('popup-active');
		$(addName).css('position','fixed');
		$(addName).css('display','block');
		
	}
	
	// 팝업 내용 바꾸는 로직
	function setPopupAnsRate(ansSeq){
		
		$.ajax({
			url : '/qna/selectAnswer',
			contentType: 'application/json',
			data : JSON.stringify({
				  'ansSeq' : ansSeq
			}),
			type : 'POST',
			async : false,
			success : function (result, textStatus, xhr){
				//console.log(xhr.status);
				//console.log(result);
				
				var answer = result.answer;
				var qstList = answer.qstList;
				var rate = answer.ansRate != null? answer.ansRate : 0;	// 별점
				var decimal = rate.toFixed(1) - Math.floor(rate);	// 별점의 소수점
				
				if(decimal > 0.5){
					// 소수점이 0.5보다 크면 반올림
					rate = Math.round(rate);
				} else if(decimal == 0.5){
					rate = Math.floor(rate) + decimal;
				} else{
					// 소수점이 0.5보다 작으면 내림
					rate = Math.floor(rate);
				}
				
				
				// 답변 및 평점
				$('#popup1-answer-rate').empty();
				
				var answerRateHtml = '';
				
              	answerRateHtml += '<div class="popup-answer">';
                answerRateHtml += '	<p>' + answer.ansContent + '</p>';
              	answerRateHtml += '</div>';
				answerRateHtml += '<div class="popup-gpa">';
				answerRateHtml += '	<span>' + rate + '</span>';
				answerRateHtml += '	<div class="starRevAvr">';
				
				for(var i=1; i<=10; i++){
					var starClass = '';
					if(i%2 == 1){
						starClass = 'starAvrR1';
					} else{
						starClass = 'starAvrR2';
					}
					
					if(i<=rate){
						answerRateHtml += '		<span class="' + starClass + ' on"></span>';
					} else{
						answerRateHtml += '		<span class="' + starClass + '"></span>';
					}
					
				}
				
				answerRateHtml += '	</div>';
				answerRateHtml += '</div>';
				
				$('#popup1-answer-rate').append(answerRateHtml);
				
				
				// 의견 리스트
				$('.table-review').empty();
				
				var qstOpinionHtml = '';
				
				qstOpinionHtml += '<colgroup>';
                qstOpinionHtml += '  <col width="7%" /> <col width="7%" /> <col width="36%" /> <col width="37%" /> <col width="13%" />';
                qstOpinionHtml += '</colgroup>';
                qstOpinionHtml += '<thead>';
                qstOpinionHtml += '<tr>';
                qstOpinionHtml += '    <th>NO</th>';
                qstOpinionHtml += '    <th>평점</th>';
                qstOpinionHtml += '    <th>질문내용</th>';
                qstOpinionHtml += '    <th>의견</th>';
                qstOpinionHtml += '    <th>작성일</th>';
                qstOpinionHtml += '</tr>';
                qstOpinionHtml += '</thead>';
				
				if(qstList.length > 0){
					for(var i=qstList.length; i>0; i--){
						
						var question = qstList[i-1];
						var date = new Date(question.creDt).toISOString().slice(0, 10);
						
						
		                qstOpinionHtml += '<tr>';
		                qstOpinionHtml += '    <td class="ct">' + i + '</td>';
		                qstOpinionHtml += '    <td class="ct">' + question.qstAnsRate + '</td>';
		                qstOpinionHtml += '    <td><div>' + question.qstContent + '</div></td>';
		                qstOpinionHtml += '    <td><div>' + question.qstOpinion + '</div></td>';
		                qstOpinionHtml += '    <td>' + date + '</td>';
		                qstOpinionHtml += '</tr>';
					}
				}
				
				$('.table-review').append(qstOpinionHtml);
				
				// 의견 및 평점 남기기
				$('#popup2-answer').empty();
				$('#popup2-answer').append('<p id="qst-Content">' + answer.ansContent + '</p>');
				
				
			},
			error: function(jqXHR, textStatus, errorThrown){
			    console.log(textStatus + ' : ' + jqXHR.status + ' ' + errorThrown);
			}
		});
		
	}
	
	// 질문 채팅목록 초기화 버튼 클릭 이벤트
	$('.btn-reset').click(function(){
		
		var result = confirm('초기화 하시겠습니까?');
		
		if(result){
			// 채팅 내역 초기화
			$('.chat > ul > li').not(':first').remove();
			$('#qstText').val('');
			
			// 관련문서 탭 초기화
			$('.tab-doc > ul > li').remove();
			
			// 질의 응답 내용 초기화
			$('#ans-smry > ul > li').remove();
			$('#ans-smry-default').show();
			
			// 문서 내용 초기화
			$('#ans-doc-smry').hide();
			$('#ans-doc-smry-default').show();
			
			// 답변 문서 키워드 검색 초기화
			$('#ans-doc-keyword-list > ul').empty();
			$('#ans-doc-keyword-default').show();
			$('#ans-doc-keyword-input').val('');
			
			// 답변 문서 초기화
			//$('.tab-body').empty();
			//$('.tab-body').append('<p class="box-default" id="ans-doc-tab-default">답변 문서가 없습니다.</p>');
			
			// 답변 문서 default
			$('#ans-doc-tab-default').show();
			$('.ans-doc-relation').empty();
			$('.ans-doc-category').empty();
			
			// 답변리스트 초기화
			$('#ans-list-div').hide();
			$('#ans-doc-smry-default').show();
			
			qstIdx = 0;	
			ansIdx = 0;
			
			ansParamMap = [];
			ansMrcResult = [];
			ansMrcParagraphSources = [];
			ansDocResultSources = [];
			ansDocResultHitHits = [];
			ansDocDiv = [];
			ansDocKeywordSources = [];
			ansSearchInfo = [];
			ansParagraphSources = [];
		}
	});
	
})


