/*-------------------------------------------------------------------
	@문서 탭(관련문서보기 클릭 시 탭 생성)
-------------------------------------------------------------------*/
var tabDocNum = 0;
var popDocIdx = 0;
var popDocId = '';

// 관련문서보기 버튼 클릭
function layout(item) {

	$('#ans-smry-default').hide();

	var tabIdx = item.dataset.ansIdx
	var tabDoc = $('.tab-doc > ul > li');
	
	var isTab = $('.tab-doc').find('[data-tab-idx="' + tabIdx + '"]');
		
	if(isTab.length > 0){
		return;
	}
	
	// 관련문서탭이 3개 이상일 때 경고
	if (tabDoc.length >= 3) {
		alert('관련문서 탭은 3개 이상 열 수 없습니다.');
		return;
	}

	// 기존탭 off 처리
	if (tabDoc.length >= 1) {
		tabDoc.each(function(index, item) {
			$(this).addClass('tab-doc-off');
			$(this).removeClass('tab-doc-on');
		});
	}

	tabDocNum++;
	
	// 관련문서탭 생성
	$('.tab-doc > ul').append('<li class="tab-doc-on" data-tab-idx=' + tabIdx + '><a>답변 리스트-' + tabDocNum + '</a><button>&nbsp;</button></li>');
	//$('.tab-doc > ul > li').addClass('tab-doc-on');
	$('.tab-doc > ul > li > button').addClass('btn-tab-close');

	tabDoc = $('.tab-doc > ul > li');

	// 관련문서탭 닫기 버튼 이벤트
	$('.btn-tab-close').eq(tabDoc.length - 1).click(function() {
		var index = $('.btn-tab-close').index(this);
		var result = confirm('해당 탭을 닫으시겠습니까?');

		if (result) {
			var tabDoc = $('.tab-doc > ul > li');
			tabDoc.eq(index).remove();

			$('.tab-doc > ul > li').eq($('.tab-doc > ul > li').length - 1).trigger('click');
		}

		// 관련문서탭이 모두 닫혔을 때 (0개일 때)
		if ($('.tab-doc > ul > li').length == 0) {
			// 질의응답 default
			$('#ans-smry > ul').remove();
			$('#ans-smry-default').show();

			// 문서내용 default
			$('#ans-list-div').hide();
			$('#ans-doc-smry-default').show();

			// 답변 문서 키워드 검색 default
			$('#ans-doc-keyword-list > ul').empty();
			$('#ans-doc-keyword-default').show();
			$('#ans-doc-keyword-input').val('');

			// 답변 문서 default
			//$('.tab-body').empty();
			//$('.tab-body').append('<p class="box-default" id="ans-doc-tab-default">답변 문서가 없습니다.</p>');
			
			// 답변 문서 default
			$('#ans-doc-tab-default').show();
			$('.ans-doc-relation').empty();
			$('.ans-doc-category').empty();

			$('#similarity-button').trigger('click');
		}
	});

	// 관련문서탭 클릭 이벤트
	$('.tab-doc > ul > li').eq(tabDoc.length - 1).click(function() {
		
		$('#similarity-button').trigger('click');

		$('#ans-doc-smry-default').hide();
		$('#ans-doc-smry').show();

		$('.tab-doc > ul > li').removeClass('tab-doc-on');
		$('.tab-doc > ul > li').addClass('tab-doc-off');
		$(this).attr('class', 'tab-doc-on');

		var idx = $(this).data('tabIdx');

		// 질의응답 내용 셋팅
		setAnsSmry(idx);

		// 문서 내용 셋팅
		//setDocContent(idx, null, 'init');
		setDocContent2(idx);

		// 답변 문서 셋팅
		setAnsDoc(idx);


		$('.btn-open').trigger('click');
	});

	$('.tab-doc > ul > li').eq(tabDoc.length - 1).trigger('click');
	$('#similarity-button').trigger('click');

}

// 질의응답 내용 셋팅
function setAnsSmry(idx) {

	var ansType = ansParamMap[idx].ansType == 'all' ? '전체' : ansParamMap[idx].ansType;
	var ansYearStart = ansParamMap[idx].ansYearStart == 'all' ? '전체' : ansParamMap[idx].ansYearStart;
	var ansYearEnd = ansParamMap[idx].ansYearEnd == 'all' ? '전체' : ansParamMap[idx].ansYearEnd;
	var question = ansParamMap[idx].question;
	var answer = ansMrcResult[idx][0].answer;
	var ansYearText = '';

	var ansTypeStr = '';
	var searchTypeStr = '';

	if (ansParamMap[idx].ansType == 'all') {
		ansTypeStr = '전체'
	} else if (ansParamMap[idx].ansType == 'pdf') {
		ansTypeStr = '백서(pdf)'
	} else if (ansParamMap[idx].ansType == 'article') {
		ansTypeStr = '뉴스(기사)'
	} else if (ansParamMap[idx].ansType == 'guide') {
		ansTypeStr = '기관 공고'
	}

	if (ansParamMap[idx].searchType == 'simple') {
		searchTypeStr = '간편검색'
	} else if (ansParamMap[idx].searchType == 'exact') {
		searchTypeStr = '정밀검색'
	}

	if (ansYearStart == 'all' && ansYearEnd == 'all') {
		ansYearText = '전체';
	} else {
		ansYearText = ansYearStart + ' ~ ' + ansYearEnd;
	}

	$('#ans-smry > ul').remove();
	$('.box01 > .list-smry').hide();
	
	// mrc 답변리스트를 mrc score순으로 정렬
	var ansMrcList = ansMrcResult[idx];
	var scoreData = ansMrcList.map(function(v){
			return v.probability;
		})
		
	scoreData = Math.max.apply(null, scoreData);

	var smryHtml = '';
	
	smryHtml += '<ul>';
	smryHtml += '<li>';
	smryHtml += '	<span>답변유형</span><a>' + ansTypeStr + '</a><span>검색유형</span><a>' + searchTypeStr + '</a><span>MAX Score :</span><a>' + scoreData + '</a>';
	smryHtml += '</li>';
	smryHtml += '<li>';
	smryHtml += '  	<span>질문</span><a>' + question + '</a>';
	smryHtml += '</li>';
	smryHtml += '<li>';
	smryHtml += '  	<span>답변</span><a>' + answer + '</a>';
	smryHtml += '</li>';
	smryHtml += '</ul>';

	$('#ans-smry').append(smryHtml);

}

function setDocContent2(idx) {

	var ansMrcList = ansMrcResult[idx];
	var ansDocSources = ansDocResultSources[idx];

	if (ansDocSources.length > 0) {
		
		var scoreData = ansMrcList.map(function(v){
			return v.probability;
		})
		
		scoreData = Math.max.apply(null, scoreData);

		$('#ansCnt').text(ansMrcList.length);	// 답변 수
		$('#ansDocCnt').text(ansDocSources.length);	// 연관문서 수
		$('#ansMaxScore').text(scoreData);	// 문서 max score
		
		$('#continue-search').data('ans-idx', idx);
		$('#ans-doc-smry-default').hide();
		$('#ans-list-div').show();
		
		setDocMrcResult(idx, 1);
	}
}

function setDocMrcResult(idx, pageIdx){
	
	var ansMrcList = ansMrcResult[idx];
	//var ansDocSources = ansDocResultSources[idx];
	var fromSourceIdx = (pageIdx * 10 - 10);
	var toSourceIdx = (pageIdx * 10);

	if ((pageIdx * 10) >= ansMrcList.length) {
		toSourceIdx = ansMrcList.length;
	}
	
	$('.answerlist').empty();

	var ansListHtml = '';

	ansListHtml += '<table class="table-answer">';

	for (var i = fromSourceIdx; i < toSourceIdx; i++) {
		//ansMrcList[i].docId;
		
		var ansDocSource;
		ansDocSource = ansDocResultSources[idx].filter(function(e){
		    return e.doc_id_STR === ansMrcList[i].docId;
		});
	
		var publisher = ansDocSource[0].publisher_STR == null? '' : ansDocSource[0].publisher_STR;
	
		ansListHtml += '<tr>';
		ansListHtml += '	<td>';
		ansListHtml += '		<div class="answer-left">';
		ansListHtml += '			<ul>';
		ansListHtml += '				<li><a class="answer-score">SCORE : ' + ansMrcList[i].probability + '</a></li>';
		ansListHtml += '				<li class="answer-txt">' + ansMrcList[i].answer + '</li>';
		ansListHtml += '				<li class="answer-info">문서제목 : ' + ansDocSource[0].doc_article_name_STR + '</li>';
		ansListHtml += '				<li class="answer-info">발행기관 : ' + publisher + ' / 발행일자 : ' + ansDocSource[0].published_date_DT.substring(0, 10) + '</li>';
		ansListHtml += '			</ul>';
		ansListHtml += '		</div>';
		ansListHtml += '		<div class="answer-right">';
		ansListHtml += '			<ul>';
		ansListHtml += '				<li><button type="button" class="btn-mini btn-doc" data-ans-idx=' + i + '>원문보기</button></li>';
		ansListHtml += '			</ul>';
		ansListHtml += '		</div>';
		ansListHtml += '	</td>';
		ansListHtml += '</tr>';
	}

	ansListHtml += '</table>';

	$('.answerlist').append(ansListHtml);
	
	// 페이징
	var totalPage = parseInt(ansMrcList.length / 10) + (ansMrcList.length % 10 == 0 ? 0 : 1);
	var pageRange = 5;

	if (pageRange >= totalPage) {
		pageRange = totalPage;
	}

	var fromPageNum = pageRange * parseInt((pageIdx - 1) / pageRange) + 1;
	var toPageNum = pageRange * (parseInt((pageIdx - 1) / pageRange) + 1);
	var prePageNum = pageIdx - 1;
	var nextPageNum = pageIdx + 1;
	
	var ansDocPageHtml = '';

	ansDocPageHtml += '<div class="page-num mt-15 mb-5" id="ans-doc-mrc-page">';
	ansDocPageHtml += '	<div class="inner">';
	ansDocPageHtml += '  		<li><a onclick="setDocMrcResult(' + idx + ', ' + 1 + ');" class="btn-prev-first">◀◀</a></li>';
	ansDocPageHtml += '  		<li><a onclick="checkVaildMrcResultPage(' + idx + ', ' + parseInt(prePageNum) + ');" class="btn-prev">◀</a></li>';
	ansDocPageHtml += '      	<li>';

	for (var i = fromPageNum; i <= toPageNum; i++) {
		var currentPage = '';

		if (i == pageIdx) {
			currentPage = 'class="current"';
		}

		if (i <= totalPage) {
			ansDocPageHtml += '        		<a onclick="setDocMrcResult(' + idx + ', ' + i + ');"' + currentPage + '>' + i + '</a>';
		} else {
			ansDocPageHtml += '        		<a onclick="checkVaildMrcResultPage(' + idx + ', ' + i + ');">' + i + '</a>';
		}
	}

	ansDocPageHtml += '      	</li>';
	ansDocPageHtml += '  		<li><a onclick="checkVaildMrcResultPage(' + idx + ', ' + parseInt(nextPageNum) + ');" class="btn-next">▶</a></li>';
	ansDocPageHtml += '  		<li><a onclick="setDocMrcResult(' + idx + ', ' + totalPage + ');" class="btn-next-end">▶▶</a></li>';
	ansDocPageHtml += '	</div>';
	ansDocPageHtml += '</div>';
	
	$('.answerlist').append(ansDocPageHtml);
	
	//'원문보기'
	$('.btn-doc').click(function(){
		var addName = '.popup-wrap-5';
		popupAdd(addName);
		$("body").addClass('layer-open'); //overflow:hidden 추가

		var ansIdx = $(this).data('ansIdx');
		var mrcDocId = ansMrcResult[idx][ansIdx].docId;
		
		setTimeout(function () {
			setAnsPopupContent(idx, ansIdx, null, mrcDocId);
		}, 1)
	});
}


function setAnsPopupContent(idx, ansIdx, type, mrcDocId) {
	$('#ans-popup-left-cnd').empty();
	
	var ansParam = ansParamMap[idx];
	var ansDocSource;
	//var ansDocSource = ansDocResultSources[idx].find(findDocIdFromDocSources);
	
	var sourceIndex = ansDocResultSources[idx].findIndex(function(item, i){
		return item.doc_id_STR === ansMrcResult[idx][ansIdx].docId;
	});
	
	if(mrcDocId == null){
		ansDocSource = ansDocResultSources[idx][sourceIndex];
	} else{
		var docSource = ansDocResultSources[idx].filter(function(e){
		    return e.doc_id_STR === mrcDocId;
		})
		
		ansDocSource = docSource[0];
	}
	
	/*
	if(type == null){
		ansDocSource = ansDocResultSources[idx][ansIdx];
	} else{
		ansDocSource = ansDocResultSources[idx][ansIdx];
	}*/
	
	var ansPopupContentHtml = '';

	var ansTypeStr = '';
	var searchTypeStr = '';
	var questionStr = ansParam.question;
	var answerStr = ansMrcResult[idx][0].answer;
	var mrcScoreStr = ansMrcResult[idx][0].probability;
	
	if (ansParam.ansType == 'all') {
		ansTypeStr = '전체'
	} else if (ansParam.ansType == 'pdf') {
		ansTypeStr = '백서(pdf)'
	} else if (ansParam.ansType == 'article') {
		ansTypeStr = '뉴스(기사)'
	} else if (ansParam.ansType == 'guide') {
		ansTypeStr = '기관 공고'
	}

	if (ansParam.searchType == 'simple') {
		searchTypeStr = '간편검색'
	} else if (ansParam.searchType == 'exact') {
		searchTypeStr = '정밀검색'
	}

	ansPopupContentHtml += '<ul>';
	ansPopupContentHtml += '	<li>';
	ansPopupContentHtml += '		<span>검색조건 :</span><a>' + ansTypeStr + ', ' + searchTypeStr + '</a>';
	ansPopupContentHtml += '	</li>';
	ansPopupContentHtml += '	<li>';
	ansPopupContentHtml += '		<span>질문 :</span><a>' + questionStr + '</a>';
	ansPopupContentHtml += '	</li>';
	ansPopupContentHtml += '	<li>';
	ansPopupContentHtml += '		<span>답변 :</span><a>' + answerStr + '</a>';
	ansPopupContentHtml += '	</li>';
	ansPopupContentHtml += '	<li>';
	ansPopupContentHtml += '		<span>SCORE : </span><a>' + mrcScoreStr + '</a>';
	ansPopupContentHtml += '	</li>';
	ansPopupContentHtml += '	<li>';
	ansPopupContentHtml += '		<span>원문 :</span><a>' + ansDocResultSources[idx][0].doc_article_name_STR + '</a>';
	ansPopupContentHtml += '	</li>';
	ansPopupContentHtml += '</ul>';

	$('#ans-popup-left-cnd').append(ansPopupContentHtml);

	var mrcListInDoc = [];

	for (var i = 0; i < ansMrcResult[idx].length; i++) {
		var docId = ansDocSource.doc_id_STR;

		if (docId == ansMrcResult[idx][i].docId) {
			mrcListInDoc.push(ansMrcResult[idx][i]);
		}
	}
	
	$('#ans-popup-ans-list > div > table').empty();
	$('#ans-popup-ans-list > h1').text('해당 원문 내 답변 문장 리스트 (' + mrcListInDoc.length + '개)');
	
	
	setAnsPopupMrcList(idx, ansIdx, 1);
	
	var pdfUrl = ansDocSource.doc_pdf_url_STR;
	var pdfPageNum = mrcListInDoc[0].pageNumber;
	
	if(pdfPageNum == ''){
		pdfPageNum = '1';
	}
	
	// pdf 생성
	$("#div_load_image").show();
	setTimeout(function () {
		setPopupPdf(pdfUrl, pdfPageNum, docId);
	},0.5);
	
	$('.ans-popup-ans-content > ul').remove();
	
	var publisher = ansDocSource.publisher_STR == null? '' : ansDocSource.publisher_STR;
	var ansPopupContentHtml2 = '';
	
	ansPopupContentHtml2 += '<ul>';
	ansPopupContentHtml2 += '	<li>';
	ansPopupContentHtml2 += '		<span>제목</span><a>' + ansDocSource.doc_article_name_STR + '</a>';
	ansPopupContentHtml2 += '	</li>';
	ansPopupContentHtml2 += '	<li>';
	ansPopupContentHtml2 += '		<span>발행기관</span><a>' + publisher + '</a><span>발행일자</span><a>' + ansDocSource.published_date_DT.substring(0, 10) + '</a>';
	ansPopupContentHtml2 += '	</li>';
	ansPopupContentHtml2 += '</ul>';
	
	$('.ans-popup-ans-content').prepend(ansPopupContentHtml2);
	
}

function setAnsPopupMrcList(idx, ansIdx, pageIdx){

	$('#ans-popup-ans-list > div > table').empty();
	$('#ans-popup-ans-list > div > div').remove();
	
	var ansMrc = ansMrcResult[idx][ansIdx];
	//var ansDocSource = ansDocResultSources[idx][ansIdx];
	var ansDocSource;
	
	var mrcListInDoc = [];
	var docId = ansMrc.docId;
	
	ansDocSource = ansDocResultSources[idx].filter(function(e){
	    return e.doc_id_STR === docId;
	})
	
	for (var i = 0; i < ansMrcResult[idx].length; i++) {
		//docId = ansDocSource.doc_id_STR;

		if (docId == ansMrcResult[idx][i].docId) {
			mrcListInDoc.push(ansMrcResult[idx][i]);
		}
	}

	var fromSourceIdx = (pageIdx * 5 - 5);
	var toSourceIdx = (pageIdx * 5);

	if ((pageIdx * 5) >= mrcListInDoc.length) {
		toSourceIdx = mrcListInDoc.length;
	}

	var totalPage = parseInt(mrcListInDoc.length / 5) + (mrcListInDoc.length % 5 == 0 ? 0 : 1);
	var pageRange = 3;

	if (pageRange >= totalPage) {
		pageRange = totalPage;
	}
	
	var ansPopupContentListHtml = '';
	
	for (var i=fromSourceIdx; i<toSourceIdx; i++) {
		var activeClass = '';
		
		if(mrcListInDoc[i].answer == ansMrc.answer && mrcListInDoc[i].sentenceIndex == ansMrc.sentenceIndex && mrcListInDoc[i].probability == ansMrc.probability){
	        activeClass = 'class="answer-active"';
        }
	
        ansPopupContentListHtml += '<tr ' + activeClass + '>';
        ansPopupContentListHtml += '	<td>';
        ansPopupContentListHtml += '		<ul>';
        ansPopupContentListHtml += '  	 		<li><a class="answer-score">SCORE : ' + mrcListInDoc[i].probability + '</a></li>';
        ansPopupContentListHtml += '    		<li class="answer-txt">' + mrcListInDoc[i].answer + '</li>';
        ansPopupContentListHtml += '  		</ul>';
        ansPopupContentListHtml += '    </td>';
        ansPopupContentListHtml += '</tr>';
	}
	
	$('#ans-popup-ans-list > div > table').append(ansPopupContentListHtml);

	var fromPageNum = pageRange * parseInt((pageIdx - 1) / pageRange) + 1;
	var toPageNum = pageRange * (parseInt((pageIdx - 1) / pageRange) + 1);
	var prePageNum = pageIdx - 1;
	var nextPageNum = pageIdx + 1;
	
	var mrcListInDocPageHtml = '';

	mrcListInDocPageHtml += '<div class="page-num mt-15 mb-5" id="ans-doc-relation-page">';
	mrcListInDocPageHtml += '	<div class="inner">';
	mrcListInDocPageHtml += '  		<li><a onclick="setAnsPopupMrcList(' + idx + ', ' + ansIdx + ', ' + 1 + ');" class="btn-prev-first">◀◀</a></li>';
	mrcListInDocPageHtml += '  		<li><a onclick="checkVaildMrcListPage(' + idx + ', ' + ansIdx + ', ' + parseInt(prePageNum) + ');" class="btn-prev">◀</a></li>';
	mrcListInDocPageHtml += '      	<li>';

	for (var i = fromPageNum; i <= toPageNum; i++) {
		var currentPage = '';

		if (i == pageIdx) {
			currentPage = 'class="current"';
		}

		if (i <= totalPage) {
			mrcListInDocPageHtml += '        		<a onclick="setAnsPopupMrcList(' + idx + ', ' + ansIdx + ', ' + i + ');"' + currentPage + '>' + i + '</a>';
		} else {
			mrcListInDocPageHtml += '        		<a onclick="checkVaildMrcListPage(' + idx + ', ' + ansIdx + ', ' + i + ');">' + i + '</a>';
		}
	}

	mrcListInDocPageHtml += '      	</li>';
	mrcListInDocPageHtml += '  		<li><a onclick="checkVaildMrcListPage(' + idx + ', ' + ansIdx + ', ' + parseInt(nextPageNum) + ');" class="btn-next">▶</a></li>';
	mrcListInDocPageHtml += '  		<li><a onclick="setAnsPopupMrcList(' + idx + ', ' + ansIdx + ', ' + totalPage + ');" class="btn-next-end">▶▶</a></li>';
	mrcListInDocPageHtml += '	</div>';
	mrcListInDocPageHtml += '</div>';

	$('#ans-popup-ans-list > div').append(mrcListInDocPageHtml);
	
	var pdfUrl = ansDocSource[0].doc_pdf_url_STR;
	var pdfPageNum = mrcListInDoc[0].pageNumber;
	
	if(pdfPageNum == ''){
		pdfPageNum = '1';
	}
	
	// 팝업 해당 원문 내 답변 문장 리스트 클릭 이벤트
	$('#ans-popup-ans-list > div > table > tr').click(function() {
		$('#ans-popup-ans-list > div > table > tr').removeClass('answer-active');
		$(this).addClass('answer-active');
		
		pdfPageNum = mrcListInDoc[($(this).index() + (pageIdx * 3)) - 3].pageNumber;
		// pdf 생성
		
		setTimeout(function () {
			setPopupPdf(pdfUrl, pdfPageNum, docId);
		},0.5);
	});

}

// pdf 생성
function setPopupPdf(pdfUrl, pdfPageNum, docId){
	
	if(popDocId == docId){
		$('#pdfboxDiv > div > iframe')[0].contentWindow.PDFViewerApplication.page = parseInt(pdfPageNum);
		$("#div_load_image").hide();
	} else{
		popDocId = docId;
		$('#pdfboxDiv').empty();
		// pdf ajax
		$.ajax({
			url: '/qna/getPdf',
			contentType: 'application/json',
			data: JSON.stringify({
				'url': pdfUrl
			}),
			type: 'POST',
			async: false,
			success: function(result, textStatus, xhr) {
				$("#div_load_image").hide();
				var options = {
					pdfOpenParams: {
						navpanes: 0,
						toolbar: 0,
						statusbar: 0,
						view: "FitV",
						//pagemode: "thumbs",
						page: pdfPageNum
					},
					forcePDFJS: true,
					PDFJS_URL: "/pdf/web/viewer.html"
				};
	
				//var base64String = 'JVBERi0xLjcKCjEgMCBvYmogICUgZW50cnkgcG9pbnQKPDwKICAvVHlwZSAvQ2F0YWxvZwogIC9QYWdlcyAyIDAgUgo+PgplbmRvYmoKCjIgMCBvYmoKPDwKICAvVHlwZSAvUGFnZXMKICAvTWVkaWFCb3ggWyAwIDAgMjAwIDIwMCBdCiAgL0NvdW50IDEKICAvS2lkcyBbIDMgMCBSIF0KPj4KZW5kb2JqCgozIDAgb2JqCjw8CiAgL1R5cGUgL1BhZ2UKICAvUGFyZW50IDIgMCBSCiAgL1Jlc291cmNlcyA8PAogICAgL0ZvbnQgPDwKICAgICAgL0YxIDQgMCBSIAogICAgPj4KICA+PgogIC9Db250ZW50cyA1IDAgUgo+PgplbmRvYmoKCjQgMCBvYmoKPDwKICAvVHlwZSAvRm9udAogIC9TdWJ0eXBlIC9UeXBlMQogIC9CYXNlRm9udCAvVGltZXMtUm9tYW4KPj4KZW5kb2JqCgo1IDAgb2JqICAlIHBhZ2UgY29udGVudAo8PAogIC9MZW5ndGggNDQKPj4Kc3RyZWFtCkJUCjcwIDUwIFRECi9GMSAxMiBUZgooSGVsbG8sIHdvcmxkISkgVGoKRVQKZW5kc3RyZWFtCmVuZG9iagoKeHJlZgowIDYKMDAwMDAwMDAwMCA2NTUzNSBmIAowMDAwMDAwMDEwIDAwMDAwIG4gCjAwMDAwMDAwNzkgMDAwMDAgbiAKMDAwMDAwMDE3MyAwMDAwMCBuIAowMDAwMDAwMzAxIDAwMDAwIG4gCjAwMDAwMDAzODAgMDAwMDAgbiAKdHJhaWxlcgo8PAogIC9TaXplIDYKICAvUm9vdCAxIDAgUgo+PgpzdGFydHhyZWYKNDkyCiUlRU9G';
				var base64String = result.endcodedPdfByteArray;
				//var pdfUrl = "data:application/pdf;base64," + base64String;
				var blob = base64ToBlob(base64String, 'application/pdf');
				var url = URL.createObjectURL(blob);
	
				PDFObject.embed(url, '#pdfboxDiv', options);
	
			},
			error: function(jqXHR, textStatus, errorThrown) {
				console.log(textStatus + ' : ' + jqXHR.status + ' ' + errorThrown);
				$("#div_load_image").hide();
			}
		});
	}

}

//팝업 띄우기
function popupAdd(addName) {
	$(addName).addClass('popup-active');
	$(addName).css('position', 'fixed');
	$(addName).css('display', 'block');
}

// 문서 내용 셋팅
function setDocContent(idx, idx2, type) {

	var docObjec;
	var ansResultSourceObj;
	var paragraphPage;

	if (type == 'init') {
		docObjec = ansDocResultSources[idx][0];
		ansResultSourceObj = ansMrcParagraphSources[idx][0];
		paragraphPage = ansResultSourceObj.paragraph_page_INT;
	} else if (type == 'ansDoc') {
		docObjec = ansDocResultSources[idx][idx2];
		ansResultSourceObj = ansMrcParagraphSources[idx][idx2];
		paragraphPage = ansResultSourceObj.paragraph_page_INT;
	} else if (type == 'keyword') {
		docObjec = ansDocKeywordSources[idx];
	}

	// 보도자료가 아니면 원문보기 버튼 숨김
	if (docObjec.doc_type_STR != '보도자료') {
		$('.btn-download').hide();
	} else {
		$('.btn-download').show();
	}

	$('#ans-doc-smry > ul > li').remove();

	var docSmryHtml = '';

	docSmryHtml += '<li>';
	docSmryHtml += '  	<span>제목</span><a>' + docObjec.doc_article_name_STR + '</a>';
	docSmryHtml += '</li>';
	docSmryHtml += '<li>';
	docSmryHtml += '  	<span>발행기관</span><a>' + docObjec.publisher_STR + '</a><span>발행일자</span><a>' + docObjec.published_date_DT.substring(0, 10) + '</a>';
	docSmryHtml += '</li>';

	$('#ans-doc-smry > ul').append(docSmryHtml);

	// 새창 열기 이벤트 (원문보기 버튼)
	$('.btn-download').unbind('click').bind('click', function() {
		var url = docObjec.doc_source_STR;
		window.open(url);
	});

	// pdf ajax
	$.ajax({
		url: '/qna/getPdf',
		contentType: 'application/json',
		data: JSON.stringify({
			'url': docObjec.doc_pdf_url_STR,
			'docTitleStr': docObjec.doc_title_STR
		}),
		type: 'POST',
		async: false,
		success: function(result, textStatus, xhr) {
			//console.log(xhr.status);
			//console.log(result);

			// pdf iframe 셋팅
			//$('.pdfbox').empty();
			//$('.pdfbox').append('<iframe src="' + docObjec.doc_pdf_url_STR + '" width="100%" height="100%;" style="margin-top: 10px;"></iframe>');
			//$('.pdfbox').append('<iframe src="https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf" width="100%" height="100%;" style="margin-top: 10px;"></iframe>');

			var options = {
				pdfOpenParams: {
					navpanes: 0,
					toolbar: 0,
					statusbar: 0,
					view: "FitV",
					pagemode: "thumbs",
					page: paragraphPage
				},
				forcePDFJS: true,
				PDFJS_URL: "/pdf/web/viewer.html"
			};

			//var base64String = 'JVBERi0xLjcKCjEgMCBvYmogICUgZW50cnkgcG9pbnQKPDwKICAvVHlwZSAvQ2F0YWxvZwogIC9QYWdlcyAyIDAgUgo+PgplbmRvYmoKCjIgMCBvYmoKPDwKICAvVHlwZSAvUGFnZXMKICAvTWVkaWFCb3ggWyAwIDAgMjAwIDIwMCBdCiAgL0NvdW50IDEKICAvS2lkcyBbIDMgMCBSIF0KPj4KZW5kb2JqCgozIDAgb2JqCjw8CiAgL1R5cGUgL1BhZ2UKICAvUGFyZW50IDIgMCBSCiAgL1Jlc291cmNlcyA8PAogICAgL0ZvbnQgPDwKICAgICAgL0YxIDQgMCBSIAogICAgPj4KICA+PgogIC9Db250ZW50cyA1IDAgUgo+PgplbmRvYmoKCjQgMCBvYmoKPDwKICAvVHlwZSAvRm9udAogIC9TdWJ0eXBlIC9UeXBlMQogIC9CYXNlRm9udCAvVGltZXMtUm9tYW4KPj4KZW5kb2JqCgo1IDAgb2JqICAlIHBhZ2UgY29udGVudAo8PAogIC9MZW5ndGggNDQKPj4Kc3RyZWFtCkJUCjcwIDUwIFRECi9GMSAxMiBUZgooSGVsbG8sIHdvcmxkISkgVGoKRVQKZW5kc3RyZWFtCmVuZG9iagoKeHJlZgowIDYKMDAwMDAwMDAwMCA2NTUzNSBmIAowMDAwMDAwMDEwIDAwMDAwIG4gCjAwMDAwMDAwNzkgMDAwMDAgbiAKMDAwMDAwMDE3MyAwMDAwMCBuIAowMDAwMDAwMzAxIDAwMDAwIG4gCjAwMDAwMDAzODAgMDAwMDAgbiAKdHJhaWxlcgo8PAogIC9TaXplIDYKICAvUm9vdCAxIDAgUgo+PgpzdGFydHhyZWYKNDkyCiUlRU9G';
			var base64String = result.endcodedPdfByteArray;
			var pdfUrl = "data:application/pdf;base64," + base64String;
			var blob = base64ToBlob(base64String, 'application/pdf');
			var url = URL.createObjectURL(blob);

			PDFObject.embed(url, '#pdfboxDiv', options);

		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.log(textStatus + ' : ' + jqXHR.status + ' ' + errorThrown);
		}
	});

}

function base64ToBlob(base64String, type = "application/octet-stream") {
	var binStr = atob(base64String);
	var len = binStr.length;
	var arr = new Uint8Array(len);
	for (let i = 0; i < len; i++) {
		arr[i] = binStr.charCodeAt(i);
	}
	return new Blob([arr], { type: type });
}

// 답변 문서 셋팅
function setAnsDoc(idx) {
	// 답변 문서 카테고리 doc_type_STR
	// 0 백서
	// 1 뉴스
	// 2 기관공고

	$('#ans-doc-tab-default').hide();

	setAnsDocRelation(idx, 1);
	setAnsDocCategory(idx, 1);

}

function setAnsDocRelation(idx, pageIdx) {
	// 답변 문서 카테고리 doc_type_STR
	// 0 백서
	// 1 뉴스
	// 2 기관공고

	// (n*index-n(한페이지 보여줄 게시물 수)) ~ (index*n-1) or 배열사이즈
	// 들어온 index 값으로 class="current" 주고
	// 

	//$('.ans-doc-relation').show();
	$('#ans-doc-tab1-table > tr').remove();
	$('#ans-doc-relation-page').remove();

	var ansDocSources = ansDocResultSources[idx];
	var fromSourceIdx = (pageIdx * 10 - 10);
	var toSourceIdx = (pageIdx * 10);

	if ((pageIdx * 10) >= ansDocSources.length) {
		toSourceIdx = ansDocSources.length;
	}

	var totalPage = parseInt(ansDocSources.length / 10) + (ansDocSources.length % 10 == 0 ? 0 : 1);
	var pageRange = 5;

	if (pageRange >= totalPage) {
		pageRange = totalPage;
	}

	var fromPageNum = pageRange * parseInt((pageIdx - 1) / pageRange) + 1;
	var toPageNum = pageRange * (parseInt((pageIdx - 1) / pageRange) + 1);
	var prePageNum = pageIdx - 1;
	var nextPageNum = pageIdx + 1;


	$('#ans-doc-tab-default').hide();
	$('.ans-doc-relation').empty();

	var ansDocContentHtml = '';
	
	ansDocContentHtml += '<table class="table-doc" id="ans-doc-tab1-table">';
    ansDocContentHtml += '    <colgroup>';
    ansDocContentHtml += '      <col width="22%" /> <col width="60%" /> <col width="28%" />';
    ansDocContentHtml += '    </colgroup>';
    ansDocContentHtml += '    <thead>';
    ansDocContentHtml += '        <tr>';
    ansDocContentHtml += '            <th>분류</th>';
    ansDocContentHtml += '            <th>문서명</th>';
    ansDocContentHtml += '            <th>MAXScore</th>';
    ansDocContentHtml += '        </tr>';
    ansDocContentHtml += '    </thead>';

	for (var i = fromSourceIdx; i < toSourceIdx; i++) {
		var sourceDocId = ansDocSources[i].doc_id_STR;
		var sourceIndex = ansMrcResult[idx].findIndex(function(item, i){
		  	return item.docId === sourceDocId;
		});
		
		ansDocContentHtml += '		<tr>';
		ansDocContentHtml += '			<td>' + ansDocSources[i].doc_type + '</td>';
		ansDocContentHtml += '			<td><a onclick="activeDocPopup(' + idx + ', ' + sourceIndex + ',\'ansDoc\', ' + null + ');">' + ansDocSources[i].doc_article_name_STR + '</a></td>';
		ansDocContentHtml += '			<td>' + ansDocSources[i].mrcScore + '</td>';
		ansDocContentHtml += '		</tr>';
	}
	
	ansDocContentHtml += '</table>';

	$('.ans-doc-relation').append(ansDocContentHtml);

	var ansDocPageHtml = '';

	ansDocPageHtml += '<div class="page-num mt-15 mb-5" id="ans-doc-relation-page">';
	ansDocPageHtml += '	<div class="inner">';
	ansDocPageHtml += '  		<li><a onclick="setAnsDocRelation(' + idx + ', ' + 1 + ');" class="btn-prev-first">◀◀</a></li>';
	ansDocPageHtml += '  		<li><a onclick="checkVaildRelationPage(' + idx + ', ' + parseInt(prePageNum) + ');" class="btn-prev">◀</a></li>';
	ansDocPageHtml += '      	<li>';

	for (var i = fromPageNum; i <= toPageNum; i++) {
		var currentPage = '';

		if (i == pageIdx) {
			currentPage = 'class="current"';
		}

		if (i <= totalPage) {
			ansDocPageHtml += '        		<a onclick="setAnsDocRelation(' + idx + ', ' + i + ');"' + currentPage + '>' + i + '</a>';
		} else {
			ansDocPageHtml += '        		<a onclick="checkVaildRelationPage(' + idx + ', ' + i + ');">' + i + '</a>';
		}
	}

	ansDocPageHtml += '      	</li>';
	ansDocPageHtml += '  		<li><a onclick="checkVaildRelationPage(' + idx + ', ' + parseInt(nextPageNum) + ');" class="btn-next">▶</a></li>';
	ansDocPageHtml += '  		<li><a onclick="setAnsDocRelation(' + idx + ', ' + totalPage + ');" class="btn-next-end">▶▶</a></li>';
	ansDocPageHtml += '	</div>';
	ansDocPageHtml += '</div>';

	$('.ans-doc-relation').append(ansDocPageHtml);

}

function checkVaildMrcListPage(idx, ansIdx, pageIdx) {
	
	var ansDocSource = ansDocResultSources[idx][ansIdx];
	var mrcListInDoc = [];
	
	for (var i = 0; i < ansMrcResult[idx].length; i++) {
		var docId = ansDocSource.doc_id_STR;

		if (docId == ansMrcResult[idx][i].docId) {
			mrcListInDoc.push(ansMrcResult[idx][i]);
		}
	}
	
	var totalPage = parseInt(mrcListInDoc.length / 3) + (mrcListInDoc.length % 3 == 0 ? 0 : 1);

	if (pageIdx < 1) {
		alert('첫 번째 페이지입니다.');
		return;
	}

	if (pageIdx > totalPage) {
		alert('마지막 페이지입니다.');
		return;
	}

	setAnsDocKeywordHtml(idx, pageIdx);
}

function checkVaildKeywordPage(idx, pageIdx) {

	//var ansDocSources = ansDocResultSources[idx];
	var totalPage = parseInt(ansDocKeywordSources.length / 3) + (ansDocKeywordSources.length % 3 == 0 ? 0 : 1);

	if (pageIdx < 1) {
		alert('첫 번째 페이지입니다.');
		return;
	}

	if (pageIdx > totalPage) {
		alert('마지막 페이지입니다.');
		return;
	}

	setAnsDocKeywordHtml(idx, pageIdx);
}

function checkVaildRelationPage(idx, pageIdx) {

	var ansDocSources = ansDocResultSources[idx];
	var totalPage = parseInt(ansDocSources.length / 10) + (ansDocSources.length % 10 == 0 ? 0 : 1);

	if (pageIdx < 1) {
		alert('첫 번째 페이지입니다.');
		return;
	}

	if (pageIdx > totalPage) {
		alert('마지막 페이지입니다.');
		return;
	}

	setAnsDocRelation(idx, pageIdx);
}

function checkVaildMrcResultPage(idx, pageIdx) {

	var ansDocSources = ansDocResultSources[idx];
	var totalPage = parseInt(ansDocSources.length / 10) + (ansDocSources.length % 10 == 0 ? 0 : 1);

	if (pageIdx < 1) {
		alert('첫 번째 페이지입니다.');
		return;
	}

	if (pageIdx > totalPage) {
		alert('마지막 페이지입니다.');
		return;
	}

	setDocMrcResult(idx, pageIdx);
}

function setAnsDocCategory(idx, pageIdx) {

	$('.ans-doc-category').empty();

	var ansDoc = ansDocDiv[idx];
	//var ansDocReport = ansDoc[0];
	var ansDocResearch = ansDoc[0];	// 백서 pdf
	var ansDocArticle = ansDoc[1];	// 뉴스
	var ansDocGuide = ansDoc[2];	// 기관 공고
	
	/*if (ansDocReport.length > 0) {
		$('.ans-doc-category').append('<div class="ctgry ctgry-report"></div>');
		setAnsDocCategoryContent('report', idx, pageIdx);
	}*/

	if (ansDocArticle.length > 0) {
		$('.ans-doc-category').append('<div class="ctgry ctgry-article"></div>');
		setAnsDocCategoryContent('article', idx, pageIdx);
	}

	if (ansDocResearch.length > 0) {
		$('.ans-doc-category').append('<div class="ctgry ctgry-research"></div>');
		setAnsDocCategoryContent('research', idx, pageIdx);
	}

	if (ansDocGuide.length > 0) {
		$('.ans-doc-category').append('<div class="ctgry ctgry-guide"></div>');
		setAnsDocCategoryContent('guide', idx, pageIdx);
	}

}

function setAnsDocCategoryContent(docType, idx, pageIdx) {

	var ansDoc;
	var docTypeTitle = '';

	/*if (docType == 'report') {
		ansDoc = ansDocDiv[idx][0];
		docTypeTitle = '보고서';
	} else */ 
	if (docType == 'research') {
		ansDoc = ansDocDiv[idx][0];
		docTypeTitle = '백서';
	} else if (docType == 'article') {
		ansDoc = ansDocDiv[idx][1];
		docTypeTitle = '뉴스';
	} else if (docType == 'guide') {
		ansDoc = ansDocDiv[idx][2];
		docTypeTitle = '기관 공고';
	}

	$('.ctgry-' + docType).empty();

	var fromSourceIdx = (pageIdx * 10 - 10);
	var toSourceIdx = (pageIdx * 10);

	if ((pageIdx * 10) >= ansDoc.length) {
		toSourceIdx = ansDoc.length;
	}

	var ansDocContentHtml = '';

	ansDocContentHtml += '	<h2 class="box-title-sub">';
	ansDocContentHtml += '		<span class="more">▼</span> ' + docTypeTitle + '(' + ansDoc.length + ')';
	ansDocContentHtml += '	</h2>';
	ansDocContentHtml += '	<div>';
	ansDocContentHtml += '		<table class="table-doc ans-doc-' + docType + '">';
	ansDocContentHtml += '			<colgroup>';
	ansDocContentHtml += '				<col width="22%" />';
	ansDocContentHtml += '				<col width="60%" />';
	ansDocContentHtml += '				<col width="28%" />';
	ansDocContentHtml += '			</colgroup>';
	ansDocContentHtml += '			<thead>';
	ansDocContentHtml += '				<tr>';
	ansDocContentHtml += '					<th>분류</th>';
	ansDocContentHtml += '					<th>문서명</th>';
	ansDocContentHtml += '					<th>MAXScore</th>';
	ansDocContentHtml += '				</tr>';
	ansDocContentHtml += '			</thead>';

	for (var i = fromSourceIdx; i < toSourceIdx; i++) {

		ansDocContentHtml += '		<tr>';
		ansDocContentHtml += '			<td>' + ansDoc[i].docTypeStr + '</td>';
		ansDocContentHtml += '			<td><a onclick="activeDocPopup(' + idx + ', ' + i + ',\'ansDoc\', ' + null + ');">' + ansDoc[i].docArticleNameStr + '</a></td>';
		ansDocContentHtml += '			<td>' + ansDoc[i].mrcScore + '</td>';
		ansDocContentHtml += '		</tr>';
	}

	ansDocContentHtml += '		</table>';

	var totalPage = parseInt(ansDoc.length / 10) + (ansDoc.length % 10 == 0 ? 0 : 1);
	var pageRange = 5;

	if( pageRange >= totalPage ){
		pageRange = totalPage;
	}

	var fromPageNum = pageRange * parseInt((pageIdx - 1) / pageRange) + 1;
	var toPageNum = pageRange * (parseInt((pageIdx - 1) / pageRange) + 1);
	var prePageNum = pageIdx - 1;
	var nextPageNum = pageIdx + 1;

	ansDocContentHtml += '<div class="page-num mt-15 mb-5" id="ans-doc-category-page">';
	ansDocContentHtml += '	<div class="inner">';
	ansDocContentHtml += '  		<li><a onclick="setAnsDocCategoryContent(\'' + docType + '\', ' + idx + ', ' + 1 + ');" class="btn-prev-first">◀◀</a></li>';
	ansDocContentHtml += '  		<li><a onclick="checkVaildCategoryPage(\'' + docType + '\', ' + idx + ', ' + parseInt(prePageNum) + ');" class="btn-prev">◀</a></li>';
	ansDocContentHtml += '      	<li>';

	for (var i = fromPageNum; i <= toPageNum; i++) {
		var currentPage = '';

		if (i == pageIdx) {
			currentPage = 'class="current"';
		}

		if (i <= totalPage) {
			ansDocContentHtml += '        		<a onclick="setAnsDocCategoryContent(\'' + docType + '\', ' + idx + ', ' + i + ');"' + currentPage + '>' + i + '</a>';
		} else {
			ansDocContentHtml += '        		<a onclick="checkVaildCategoryPage(\'' + docType + '\', ' + idx + ', ' + i + ');">' + i + '</a>';
		}
	}

	ansDocContentHtml += '      	</li>';
	ansDocContentHtml += '  		<li><a onclick="checkVaildCategoryPage(\'' + docType + '\', ' + idx + ', ' + parseInt(nextPageNum) + ');" class="btn-next">▶</a></li>';
	ansDocContentHtml += '  		<li><a onclick="setAnsDocCategoryContent(\'' + docType + '\', ' + idx + ', ' + totalPage + ');" class="btn-next-end">▶▶</a></li>';
	ansDocContentHtml += '	</div>';
	ansDocContentHtml += '</div>';
	ansDocContentHtml += '	</div>';

	$('.ctgry-' + docType).append(ansDocContentHtml);

	$('.ctgry-' + docType + ' > h2').click(function() {
		if ($(this).next().css('display') == 'none') {
			$(this).next().show();
			$(this).find('.more').text('▲');
		} else {
			$(this).next().hide();
			$(this).find('.more').text('▼');
		}
	});
}

function checkVaildCategoryPage(docType, idx, pageIdx) {

	var ansDoc;

	/*if (docType == 'report') {
		ansDoc = ansDocDiv[idx][0];
		docTypeTitle = '보고서';
	} else*/ 
	if (docType == 'research') {
		ansDoc = ansDocDiv[idx][0];
		docTypeTitle = '백서';
	} else if (docType == 'article') {
		ansDoc = ansDocDiv[idx][1];
		docTypeTitle = '뉴스';
	} else if (docType == 'guide') {
		ansDoc = ansDocDiv[idx][2];
		docTypeTitle = '기관공고';
	}

	var totalPage = parseInt(ansDoc.length / 10) + (ansDoc.length % 10 == 0 ? 0 : 1);

	if (pageIdx < 1) {
		alert('첫 번째 페이지입니다.');
		return;
	}

	if (pageIdx > totalPage) {
		alert('마지막 페이지입니다.');
		return;
	}

	setAnsDocCategoryContent(docType, idx, pageIdx);
}

// 답변 문서 내 키워드 검색 셋팅
function setAnsDocKeyword(idx, pageIdx) {

	var keywordText = $('#ans-doc-keyword-input').val();
	var ansDocSources = ansDocResultSources[idx];
	
	ansDocKeywordSources = [];

	for (var i = 0; i < ansDocSources.length; i++) {
	
		var ansDoc = ansDocSources[i];
		var sourceDocId = ansDoc.doc_id_STR;
		var docStr = ansDoc.doc_summary_STR;
		var sourceObj = {};
		
		var sourceIndex = ansMrcResult[idx].findIndex(function(item, i){
			return item.docId === sourceDocId;
		});

		if (docStr.includes(keywordText)) {
			//ansDocKeywordSources.push(ansDoc);
			
			sourceObj.idx = sourceIndex;
			sourceObj.source = ansDoc;
			
			ansDocKeywordSources.push(sourceObj);
		}
	}

	if (ansDocKeywordSources.length > 0) {
	
		setAnsDocKeywordHtml(idx, pageIdx);

	} else{
		$('#ans-doc-keyword-list > ul').remove();
		$('#ans-doc-keyword-list > div').remove();
		$('#ans-doc-keyword-default').show();
	}

}

function setAnsDocKeywordHtml(idx, pageIdx){

	$('#ans-doc-keyword-list > ul').remove();
	$('#ans-doc-keyword-list > div').remove();
	$('#ans-doc-keyword-default').hide();

	var keywordText = $('#ans-doc-keyword-input').val();
	var ansDocKeywordHtml = '';
	
	ansDocKeywordHtml += '<ul>';
	
	var fromSourceIdx = (pageIdx * 3 - 3);
	var toSourceIdx = (pageIdx * 3);
	
	if ((pageIdx * 3) >= ansDocKeywordSources.length) {
		toSourceIdx = ansDocKeywordSources.length;
	}
	
	for (var i = fromSourceIdx; i < toSourceIdx; i++) {
		var sourceIdx = ansDocKeywordSources[i].idx;
		var ansDocSortSource = ansDocKeywordSources[i].source;
		var docTitle = ansDocSortSource.doc_article_name_STR;
		var docSmry = ansDocSortSource.doc_summary_STR;
		var docSmryLength = docSmry.length;
		var strLength = 50;
		var ellipsis = '';
		
		if (docSmryLength > strLength) {
			docSmryLength = strLength;
			ellipsis = '...';
		}

		if (docTitle.length > strLength) {
			docTitle = docTitle.substring(0, docSmryLength) + ellipsis;
		}

		var keywordIdx = docSmry.indexOf(keywordText);

		if (keywordIdx <= 20) {
			docSmry = docSmry.substring(0, docSmryLength) + ellipsis;
		} else {

			if (docSmry.length - keywordIdx > 50) {
				docSmry = docSmry.substring(keywordIdx, keywordIdx + 50) + ellipsis;
			} else {
				docSmry = docSmry.substring(keywordIdx, docSmry.length);
			}

		}
		
		docSmry = docSmry.replaceAll(keywordText, '<span style="font-weight:bold;">' + keywordText + '</span>');
		
		ansDocKeywordHtml += '<li>';
		ansDocKeywordHtml += '  	<a onclick="activeDocPopup(' + idx + ', ' + sourceIdx + ',\'ansDoc\', ' + null + ');">' + docTitle + '</a>';
		ansDocKeywordHtml += '  	<p>' + docSmry + '</p>';
		ansDocKeywordHtml += '</li>';
	}
	
	ansDocKeywordHtml += '</ul>';

	$('#ans-doc-keyword-list').prepend(ansDocKeywordHtml);
	
	//var ansDocSources = ansDocResultSources[idx];

	if ((pageIdx * 3) >= ansDocKeywordSources.length) {
		toSourceIdx = ansDocKeywordSources.length;
	}

	var totalPage = parseInt(ansDocKeywordSources.length / 3) + (ansDocKeywordSources.length % 3 == 0 ? 0 : 1);
	var pageRange = 5;

	if (pageRange >= totalPage) {
		pageRange = totalPage;
	}

	var fromPageNum = pageRange * parseInt((pageIdx - 1) / pageRange) + 1;
	var toPageNum = pageRange * (parseInt((pageIdx - 1) / pageRange) + 1);
	var prePageNum = pageIdx - 1;
	var nextPageNum = pageIdx + 1;
	
	var ansDocKeywordPageHtml = '';

	ansDocKeywordPageHtml += '<div class="page-num mt-15 mb-5" id="ans-doc-relation-page">';
	ansDocKeywordPageHtml += '	<div class="inner">';
	ansDocKeywordPageHtml += '  		<li><a onclick="setAnsDocKeywordHtml(' + idx + ', ' + 1 + ');" class="btn-prev-first">◀◀</a></li>';
	ansDocKeywordPageHtml += '  		<li><a onclick="checkVaildKeywordPage(' + idx + ', ' + parseInt(prePageNum) + ');" class="btn-prev">◀</a></li>';
	ansDocKeywordPageHtml += '      	<li>';

	for (var i = fromPageNum; i <= toPageNum; i++) {
		var currentPage = '';

		if (i == pageIdx) {
			currentPage = 'class="current"';
		}

		if (i <= totalPage) {
			ansDocKeywordPageHtml += '        		<a onclick="setAnsDocKeywordHtml(' + idx + ', ' + i + ');"' + currentPage + '>' + i + '</a>';
		} else {
			ansDocKeywordPageHtml += '        		<a onclick="checkVaildKeywordPage(' + idx + ', ' + i + ');">' + i + '</a>';
		}
	}

	ansDocKeywordPageHtml += '      	</li>';
	ansDocKeywordPageHtml += '  		<li><a onclick="checkVaildKeywordPage(' + idx + ', ' + parseInt(nextPageNum) + ');" class="btn-next">▶</a></li>';
	ansDocKeywordPageHtml += '  		<li><a onclick="setAnsDocKeywordHtml(' + idx + ', ' + totalPage + ');" class="btn-next-end">▶▶</a></li>';
	ansDocKeywordPageHtml += '	</div>';
	ansDocKeywordPageHtml += '</div>';

	$('#ans-doc-keyword-list').append(ansDocKeywordPageHtml);

}

function activeDocPopup(idx, ansIdx, type){
	var addName = '.popup-wrap-5';
	popupAdd(addName);
	$("body").addClass('layer-open');
	
	setAnsPopupContent(idx, ansIdx, type, null);
}

function getMoreResult(){

	var ansIdx = $('#continue-search').data('ansIdx');
	var ansType = ansParamMap[ansIdx].ansType;
	var searchType = ansParamMap[ansIdx].searchType;
	var question = ansParamMap[ansIdx].question;
	var nowAnsType = ansSearchInfo[ansIdx].ansType;
	
	var result = confirm('계속해서 검색하시겠습니까?');
	
	if(result){
			
			var ansTypeIdx = ansTypeArr.indexOf(nowAnsType);
			
			if(ansType == 'all'){
				
				while(ansTypeIdx < 3){
				
					if(ansTypeIdx == 2){
						ansTypeIdx = 0;
					} else{
						ansTypeIdx += 1;
					}
					
					if(ansParagraphSources[ansIdx][ansTypeIdx].length == 0){
						continue;
					}
					
					$("#div_load_image").show();
					setTimeout(function () {
						setMoreResultData(ansIdx, ansTypeIdx);
					},1);
					
					ansSearchInfo[ansIdx].ansType = ansTypeArr[ansTypeIdx];
					
					break;
				}
				
			} else{
				$("#div_load_image").show();
				setTimeout(function () {
					setMoreResultData(ansIdx, ansTypeIdx);
				},1);
			}

	}
}

function setMoreResultData(ansIdx, ansTypeIdx){
	
	var ansIdx = $('#continue-search').data('ansIdx');
	var ansType = ansParamMap[ansIdx].ansType;
	var searchType = ansParamMap[ansIdx].searchType;
	var question = ansParamMap[ansIdx].question;
	var nowAnsType = ansSearchInfo[ansIdx].ansType;
	
	var preMrcScore = ansMrcResult[ansIdx].probability;
	var nextMrcScore = 0;
	
	ansSearchInfo[ansIdx].searchCnt[ansTypeIdx].cnt += 1;
					
	var searchCnt = ansSearchInfo[ansIdx].searchCnt[ansTypeIdx].cnt;
	var fromIdx = 0;
	var toIdx = 0;
	
	if(searchType == 'simple'){
		fromIdx = 25*searchCnt - 25;
		toIdx = 25*searchCnt;
	} else if(searchType == 'exact'){
		fromIdx = 30*searchCnt - 30;
		toIdx = 30*searchCnt;
	}
	
	if(toIdx >= ansParagraphSources[ansIdx][ansTypeIdx].length){
		toIdx = ansParagraphSources[ansIdx][ansTypeIdx].length;
	}
	
	var moreParagraphList = ansParagraphSources[ansIdx][ansTypeIdx].slice(fromIdx, toIdx);
	
	var moreAnsMrcResult;
	var moreAnsMrcParagraphSources;
	var moreAnsDocResultSources;
	
	$.ajax({
		url : '/qna/getMoreResult',
		contentType: 'application/json',
		data : JSON.stringify({
			  'moreParagraphList' : moreParagraphList,
			  'question' : question
		}),
		type : 'POST',
		async : false,
		success : function (result, textStatus, xhr){
			//console.log(xhr.status);
			//console.log(result);
			
			$("#div_load_image").hide();
			
			//ansSeq = result.ansSeq;
			moreAnsMrcResult = result.moreMrcResultMapList;
			moreAnsMrcParagraphSources = result.moreMrcParagraphSources;
			moreAnsDocResultSources = result.moreAiadDocResultSources;
			
			ansMrcResult[ansIdx].push(...moreAnsMrcResult);
			ansMrcParagraphSources[ansIdx].push(...moreAnsMrcParagraphSources);
			ansDocResultSources[ansIdx].push(...moreAnsDocResultSources);
			ansDocResultSources[ansIdx].push(...moreAnsDocResultSources);
			
			var distAiadDocResultSources;
			distAiadDocResultSources = moreAnsDocResultSources.reduce(function(acc, current) {
				if (acc.findIndex(({ doc_id_STR }) => doc_id_STR === current.doc_id_STR) === -1) {
				  acc.push(current);
				}
			  	return acc;
			}, []);
			distAnsDocResultSources[ansIdx] = [];
			distAnsDocResultSources[ansIdx].push(...distAiadDocResultSources);
			
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
			
			
		},
		error: function(jqXHR, textStatus, errorThrown){
		    console.log(textStatus + ' : ' + jqXHR.status + ' ' + errorThrown);
		    $("#div_load_image").hide();
		}
	});
	
	// 추가검색 리스트 합치고 mrc score 내림차순으로 재정렬 
	ansMrcResult[ansIdx].sort(function(a, b) {
		return parseFloat(b.probability) - parseFloat(a.probability);
	});
	
	ansMrcParagraphSources[ansIdx].sort(function(a, b) {
		return parseFloat(b.mrcScore) - parseFloat(a.mrcScore);
	});

	ansDocResultSources[ansIdx].sort(function(a, b) {
		return parseFloat(b.mrcScore) - parseFloat(a.mrcScore);
	});
	
	ansDocResultSources[ansIdx].sort(function(a, b) {
		return parseFloat(b.mrcScore) - parseFloat(a.mrcScore);
	});
	
	ansDocDiv[ansIdx] = [];
	var ansDocResearch = [];	// 백서 pdf
	var ansDocArticle = [];		// 기사
	var ansDocGuide = [];		// 공지
	var ansDoc = [];

	for(var i=0; i<ansDocResultSources[ansIdx].length; i++){
		var mrcScore = ansDocResultSources[ansIdx][i].mrcScore;
		var docTitleStr = ansDocResultSources[ansIdx][i].doc_title_STR;
		var docTypeStr = ansDocResultSources[ansIdx][i].doc_type_STR;
		var docArticleNameStr = ansDocResultSources[ansIdx][i].doc_article_name_STR;
		//var source = ansDocResultSources[ansIdx][i];
		//var docCategory = source.doc_type_STR;
		var channelNameStr = ansDocResultSources[ansIdx][i].channel_name_STR;
		
		if(channelNameStr == '파일 업로드' ||
			channelNameStr == '농림축산검역본부 도서관'){
			
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
	
	ansDoc.push(ansDocResearch);
	ansDoc.push(ansDocArticle);
	ansDoc.push(ansDocGuide);
	
	ansDocDiv[ansIdx].push(...ansDoc);
	
	// 질의응답 내용 셋팅
	setAnsSmry(ansIdx);

	// 문서 내용 셋팅
	setDocContent2(ansIdx);

	// 답변 문서 셋팅
	setAnsDoc(ansIdx);
	
	var ansHtml = '';

	if(moreAnsMrcResult.length == 0){
		ansHtml += '<li class="left">';
		ansHtml += 	'<div class="bots">';
		ansHtml += 		'<p>알맞은 답변을 찾지 못했습니다.</p>';
	    ansHtml += 	'</div>';
	    ansHtml += '</li>';

		$('.chat > ul').append(ansHtml);
		
		return;
	}
	
	var resultData = moreAnsMrcResult[0];
	var ansSeq; 
	
	$.ajax({
		url : '/qna/insertAnswer',
		contentType: 'application/json',
		data : JSON.stringify({
			  'ansContent' : resultData.answer
		 	, 'docId' : resultData.docId
		 	, 'paragraphId' : moreAnsMrcParagraphSources[0].paragraph_id_STR
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
	
	
	nextMrcScore = ansMrcResult[ansIdx].probability;
	
	// 채팅 답변
	$('.bots[data-ans-idx="' + ansIdx + '"]').empty();
	
	ansHtml += 		'<p>' + resultData.answer + '<span> (SCORE : ' +  resultData.probability + ')</span></p>';
	ansHtml +=		'<h3 class="mt-5">※ SCORE가 낮다면, 계속해서 검색해보세요.</h3>';
	ansHtml +=		'<h3>정확한 답변의 평균 SCORE는 90 전후 입니다.</h3>';
	ansHtml += 		'<button type="button" class="chat-doc" onclick="layout(this)" data-ans-idx=' + ansIdx + '>관련문서보기</button>';
	ansHtml += 		'<a data-ans-seq=' + ansSeq + '>★ 의견 및 평점 남기기</a>';
	
	$('.bots[data-ans-idx="' + ansIdx + '"]').append(ansHtml);
	
	/*
	ansHtml += '<li class="left">';
	ansHtml += 	'<div class="bots">';
	ansHtml += 		'<p>' + resultData.answer + '<span> (SCORE : ' +  resultData.probability + ')</span></p>';
	ansHtml +=		'<h3 class="mt-5">※ SCORE가 낮다면, 계속해서 검색해보세요.</h3>';
	ansHtml +=		'<h3>정확한 답변의 평균 SCORE는 90 전후 입니다.</h3>';
	ansHtml += 		'<button type="button" class="chat-doc" onclick="layout(this)" data-ans-idx=' + ansIdx + '>관련문서보기</button>';
	ansHtml += 		'<a data-ans-seq=' + ansSeq + '>★ 의견 및 평점 남기기</a>';
    ansHtml += 	'</div>';
    ansHtml += '</li>';

	$('.chat > ul').append(ansHtml);
	*/
	
	// 채팅 입력시 스크롤 최하단
	$('.chat-content').scrollTop($('.chat-content')[0].scrollHeight);
	
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
	
}

// 의견 평점 팝업 내용 바꾸는 로직
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

/*------------------------------- ------------------------------------
	@'질의응답 내용'열기-닫기
-------------------------------------------------------------------*/
function box01() {
	if ($('.box01 > .list-smry').css('display') == 'none') {
		$('.box01 > .list-smry').show();
		$('.box01 > .btn-close').show();
		$('.box01 > .btn-open').hide();
		$('.box01 > h1').css('margin-bottom', '14px');

	} else {
		$('.box01 > .list-smry').hide();
		$('.box01 > .btn-close').hide();
		$('.box01 > .btn-open').show();
		$('.box01 > h1').css('margin-bottom', '0px');
	}
}

/*------------------------------- ------------------------------------
	@'답변 문서-카테고리별' 열기-닫기
-------------------------------------------------------------------*/
$(document).ready(function() {
	$('.ctgry > h2').click(function() {
		if ($(this).next().css('display') == 'none') {
			$(this).next().show();
			$(this).find('.more').text('▲');
		} else {
			$(this).next().hide();
			$(this).find('.more').text('▼');
		}
	});
});

/*-------------------------------------------------------------------
	@'답변문서' 카테고리 탭
-------------------------------------------------------------------*/
$(document).ready(function() {
	//setAnsDocRelation(0, 1);
	//setAnsDocCategory(0, 1);
	// @'답변문서' 카테고리 탭
	$('.tab-menu ul li').click(function() {
		var tab_id = $(this).attr('data-tab');

		$('.tab-menu ul li').removeClass('is-active');
		$('.tab-content').removeClass('is-active');

		$(this).addClass('is-active');
		$("#" + tab_id).addClass('is-active');
	});

	$("#ans-doc-keyword-input").bind("keydown", function(e) {
		if (e.keyCode == 13) { // enter key
			ansDocKeywordSources = [];

			var keywordText = $('#ans-doc-keyword-input').val();

			if (keywordText == '') {
				alert('키워드를 입력해주세요.');
				return;
			}

			if (ansMrcParagraphSources.length == 0) {
				return;
			}

			var tabDoc = $('.tab-doc > ul > li');
			var idx = '';

			tabDoc.each(function(index, item) {
				if ($(item).hasClass('tab-doc-on')) {
					idx = $(item).data('tabIdx');
				}
			});

			// 답변 문서 내 키워드 검색 셋팅
			setAnsDocKeyword(idx, 1);

		}
	});

})

/*-------------------------------------------------------------------
	@정밀검색 안내창
-------------------------------------------------------------------*/
$(document).ready(function() {
	$('.radio-alert').click(function() {
		var result = alert('정밀검색은 많은 후보군에서 답변을 찾는 만큼 보다 정확한 답변을 제공할 확률이 높습니다.  \n * 검색시간 약 30초 소요');
	});
});
