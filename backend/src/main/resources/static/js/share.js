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
            const prevButton = document.querySelector('.swiper-button-prev');
            const nextButton = document.querySelector('.swiper-button-next');

            // 처음 슬라이드일 경우 왼쪽 화살표 제거
            if (this.isBeginning) {
                prevButton.style.display = 'none';
            } else {
                prevButton.style.display = 'block';
            }

            // 마지막 슬라이드일 경우 오른쪽 화살표 제거
            if (this.isEnd) {
                nextButton.style.display = 'none';
            } else {
                nextButton.style.display = 'block';
            }
        },
        slideChange: function () {
            const prevButton = document.querySelector('.swiper-button-prev');
            const nextButton = document.querySelector('.swiper-button-next');

            // 처음 슬라이드일 경우 왼쪽 화살표 제거
            if (this.isBeginning) {
                prevButton.style.display = 'none';
            } else {
                prevButton.style.display = 'block';
            }

            // 마지막 슬라이드일 경우 오른쪽 화살표 제거
            if (this.isEnd) {
                nextButton.style.display = 'none';
            } else {
                nextButton.style.display = 'block';
            }
        },
    },
});

document.addEventListener('wheel', function (event) {
    const swiperContainer = document.querySelector('.swiper');

    // Swiper 요소 위에서만 가로 스크롤 방지
    if (swiperContainer.contains(event.target) && event.deltaX !== 0) {
        event.preventDefault();
    }
}, { passive: false });

// Swiper 초기화
swiper.init(); // 수동으로 Swiper를 초기화
