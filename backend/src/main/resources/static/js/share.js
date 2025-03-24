fetch(`/staccatos/shared/${token}`)
    .then(response => response.json())
    .then(data => {
        const {
            expiredAt,
            nickname,
            staccatoImageUrls,
            staccatoTitle,
            placeName,
            address,
            visitedAt,
            feeling,
            comments,
        } = data;

        document.querySelector('.memory-title').innerText = `${nickname}님이 공유한 추억`;

        const expiredDate = new Date(expiredAt);
        const expiredYear = expiredDate.getUTCFullYear();
        const expiredMonth = expiredDate.getUTCMonth() + 1;
        const expiredDay = expiredDate.getUTCDate();
        const formattedExpiredAt = `${expiredYear}년 ${expiredMonth}월 ${expiredDay}일`;
        document.querySelector('.memory-subtitle').innerText = `${formattedExpiredAt}까지 열람할 수 있어요!`;

        const sliderWrapper = document.querySelector('.swiper-wrapper');
        sliderWrapper.innerHTML = '';
        staccatoImageUrls.forEach((url) => {
            const slideElement = document.createElement('div');
            slideElement.classList.add('swiper-slide');
            slideElement.innerHTML = `<img src="${url}" alt="Staccato Image">`;
            sliderWrapper.appendChild(slideElement);
        });

        swiper.update();

        updateNavigationButtons(swiper);

        document.querySelector('.title h1').innerText = staccatoTitle;

        document.querySelector('.details .place-name').innerText = placeName;
        document.querySelector('.details .address').innerText = address;

        const visitedDate = new Date(visitedAt);
        const visitedYear = visitedDate.getUTCFullYear();
        const visitedMonth = visitedDate.getUTCMonth() + 1;
        const visitedDay = visitedDate.getUTCDate();
        const formattedVisitedAt = `${visitedYear}년 ${visitedMonth}월 ${visitedDay}일`;
        document.querySelector('.details .visited-at').innerText = `${formattedVisitedAt}에 방문했어요`;

        const emojiPaths = {
            happy: "https://image.staccato.kr/web/share/happy",
            angry: "https://image.staccato.kr/web/share/angry",
            sad: "https://image.staccato.kr/web/share/sad",
            scared: "https://image.staccato.kr/web/share/scared",
            excited: "https://image.staccato.kr/web/share/excited"
        };

        const emojisContainer = document.querySelector('.feeling .emojis');
        emojisContainer.innerHTML = '';

        Object.keys(emojiPaths).forEach((key) => {
            const emojiImg = document.createElement('img');
            emojiImg.src = key === feeling ? `${emojiPaths[key]}.png` : `${emojiPaths[key]}-gray.png`;
            emojiImg.alt = `${key} emoji`;
            emojiImg.classList.add('emoji');
            emojisContainer.appendChild(emojiImg);
        });

        const commentsContainer = document.querySelector('.comments-list');
        commentsContainer.innerHTML = '';

        if (comments.length === 0) {
            commentsContainer.innerHTML = `
                <div class="empty-comments-container">
                    <img class="empty-comments-image" src="https://image.staccato.kr/web/share/frame.png" alt="Frame Image">
                    <p class="empty-comments-text">코멘트가 없어요! 코멘트를 달아보세요.</p>
                </div>
            `;
        } else {
            comments.forEach((comment) => {
                const commentElement = document.createElement('div');
                commentElement.classList.add('comment');
                commentElement.setAttribute('data-nickname', comment.nickname);

                const commentAlignmentClass = comment.nickname === nickname ? 'comment-right' : 'comment-left';
                const nicknameAlignmentClass = comment.nickname === nickname ? 'nickname-right' : 'nickname-left';
                const contentWrapperAlignmentClass = comment.nickname === nickname ? 'margin-right-auto' : 'margin-left-auto';

                commentElement.classList.add(commentAlignmentClass);

                const defaultProfileImage = "https://image.staccato.kr/web/share/default-profile.png";
                let profileImageUrl = comment.memberImageUrl;
                if (!profileImageUrl) {
                    profileImageUrl = defaultProfileImage;
                }

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
                        <img class="profile-picture" src="${profileImageUrl}" alt="User Image">
                    </div>
                `;
                commentsContainer.appendChild(commentElement);
            });
        }

        setupWheelScrollPrevention();
    })
    .catch((error) => {
        console.error('Error fetching data:', error);
    });

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
        forceToAxis: true,
        sensitivity: 1,
    },
    on: {
        init: function () {
            updateNavigationButtons(this);
        },
        slideChange: function () {
            updateNavigationButtons(this);
        },
    },
});

function updateNavigationButtons(swiper) {
    const prevButton = document.querySelector('.swiper-button-prev');
    const nextButton = document.querySelector('.swiper-button-next');

    if (!prevButton || !nextButton) {
        console.error("Navigation buttons not found!");
        return;
    }

    if (swiper.slides.length <= 1) {
        prevButton.style.display = 'none';
        nextButton.style.display = 'none';
        return;
    }

    prevButton.style.display = swiper.isBeginning ? 'none' : 'block';

    nextButton.style.display = swiper.isEnd ? 'none' : 'block';
}

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
