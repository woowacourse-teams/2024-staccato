// JSON 데이터 가져오기 및 처리
fetch('/share')
    .then(response => response.json())
    .then(data => {
        const {
            userName,
            expiredAt,
            momentImageUrls,
            staccatoTitle,
            placeName,
            address,
            visitedAt,
            feeling,
            comments,
        } = data;

        // Header 섹션 업데이트
        document.querySelector('.memory-title').innerText = `${userName}님이 공유한 추억`;

        const expiredDate = new Date(expiredAt);
        const expiredYear = expiredDate.getUTCFullYear();
        const expiredMonth = expiredDate.getUTCMonth() + 1;
        const expiredDay = expiredDate.getUTCDate();
        const formattedExpiredAt = `${expiredYear}년 ${expiredMonth}월 ${expiredDay}일`;
        document.querySelector('.memory-subtitle').innerText = `${formattedExpiredAt}까지 열람할 수 있어요!`;

        // Image Slider 섹션 업데이트
        const sliderWrapper = document.querySelector('.swiper-wrapper');
        sliderWrapper.innerHTML = ''; // 초기화
        momentImageUrls.forEach((url) => {
            const slideElement = document.createElement('div');
            slideElement.classList.add('swiper-slide');
            slideElement.innerHTML = `<img src="${url}" alt="Moment Image">`;
            sliderWrapper.appendChild(slideElement);
        });

        // Swiper 업데이트
        swiper.update();

        // 화살표 상태 초기화
        updateNavigationButtons(swiper);

        // Title 섹션 업데이트
        document.querySelector('.title h1').innerText = staccatoTitle;

        // Place and Date 섹션 업데이트
        document.querySelector('.details .place-name').innerText = placeName;
        document.querySelector('.details .address').innerText = address;

        const visitedDate = new Date(visitedAt);
        const visitedYear = visitedDate.getUTCFullYear();
        const visitedMonth = visitedDate.getUTCMonth() + 1;
        const visitedDay = visitedDate.getUTCDate();
        const formattedVisitedAt = `${visitedYear}년 ${visitedMonth}월 ${visitedDay}일`;
        document.querySelector('.details .visited-at').innerText = `${formattedVisitedAt}에 방문했어요`;

        // 고정된 경로 템플릿 정의
        const emojiPaths = {
            happy: "https://image.staccato.kr/web/share/happy",
            angry: "https://image.staccato.kr/web/share/angry",
            sad: "https://image.staccato.kr/web/share/sad",
            scared: "https://image.staccato.kr/web/share/scared",
            excited: "https://image.staccato.kr/web/share/excited"
        };

        // Feeling 섹션 업데이트
        const emojisContainer = document.querySelector('.feeling .emojis');
        emojisContainer.innerHTML = ''; // 초기화

        // 각 감정에 해당하는 이미지를 추가
        Object.keys(emojiPaths).forEach((key) => {
            const emojiImg = document.createElement('img');
            emojiImg.src = key === feeling ? `${emojiPaths[key]}.png` : `${emojiPaths[key]}-gray.png`;
            emojiImg.alt = `${key} emoji`;
            emojiImg.classList.add('emoji');
            emojisContainer.appendChild(emojiImg);
        });

        // Comments 섹션 업데이트
        const commentsContainer = document.querySelector('.comments-list');
        commentsContainer.innerHTML = ''; // 초기화
        comments.forEach((comment) => {
            const commentElement = document.createElement('div');
            commentElement.classList.add('comment');
            commentElement.setAttribute('data-nickname', comment.nickname);

            // 댓글 정렬 클래스 추가
            const commentAlignmentClass = comment.nickname === userName ? 'comment-right' : 'comment-left';
            const nicknameAlignmentClass = comment.nickname === userName ? 'nickname-right' : 'nickname-left';
            const contentWrapperAlignmentClass = comment.nickname === userName ? 'margin-right-auto' : 'margin-left-auto';

            commentElement.classList.add(commentAlignmentClass);

            commentElement.innerHTML = `
                <div class="content-wrapper ${contentWrapperAlignmentClass}">
                    <div class="text-section">
                        <div class="nickname-wrapper ${nicknameAlignmentClass}">
                            <p class="nickname">${comment.nickname}</p>
                        </div>
                        <div class="comment-box">
                            <p class="content">${comment.content}</p>
                        </div>
                    </div>
                    <img class="profile-picture" src="${comment.memberImageUrl}" alt="User Image">
                </div>
            `;
            commentsContainer.appendChild(commentElement);
        });

        // 휠 스크롤 방지 설정
        setupWheelScrollPrevention();
    })
    .catch((error) => {
        console.error('Error fetching data:', error);
    });

// Swiper 설정
const swiper = new Swiper('.swiper', {
    loop: false,
    speed: 500,
    navigation: {
        nextEl: '.swiper-button-next',
        prevEl: '.swiper-button-prev',
    },
    pagination: {
        el: '.swiper-pagination',
        clickable: true,
    },
    mousewheel: {
        forceToAxis: true, // 가로 또는 세로 축으로 제한
        sensitivity: 1, // 마우스 휠 민감도
    },
    on: {
        init: function () {
            updateNavigationButtons(this); // 초기화 시 화살표 상태 업데이트
        },
        slideChange: function () {
            updateNavigationButtons(this); // 슬라이드 변경 시 화살표 상태 업데이트
        },
    },
});

// 화살표 상태 업데이트 함수
function updateNavigationButtons(swiper) {
    const prevButton = document.querySelector('.swiper-button-prev');
    const nextButton = document.querySelector('.swiper-button-next');

    if (!prevButton || !nextButton) {
        console.error("Navigation buttons not found!");
        return;
    }

    // 슬라이드가 없는 경우 화살표 숨김
    if (swiper.slides.length <= 1) {
        prevButton.style.display = 'none';
        nextButton.style.display = 'none';
        return;
    }

    // 첫 번째 슬라이드일 경우 이전 버튼 숨김
    prevButton.style.display = swiper.isBeginning ? 'none' : 'block';

    // 마지막 슬라이드일 경우 다음 버튼 숨김
    nextButton.style.display = swiper.isEnd ? 'none' : 'block';
}

// Swiper 내부 마우스 휠 동작 방지
function setupWheelScrollPrevention() {
    const swiperContainer = document.querySelector('.swiper');

    document.addEventListener(
        'wheel',
        function (event) {
            if (swiperContainer.contains(event.target) && event.deltaX !== 0) {
                event.preventDefault();
            }
        },
        {passive: false}
    );
}
