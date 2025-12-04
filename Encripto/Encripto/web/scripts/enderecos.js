// ======== CARROSSEL ========
const carousel = document.querySelector('.carousel');
const nextBtn = document.querySelector('.next');
const prevBtn = document.querySelector('.prev');

if (carousel && nextBtn && prevBtn) {
  let currentIndex = 0;

  function updateCarousel() {
    const slides = document.querySelectorAll('.form-slide');
    if (slides.length === 0) return;
    if (currentIndex >= slides.length) currentIndex = 0;
    if (currentIndex < 0) currentIndex = slides.length - 1;
    carousel.style.transform = `translateX(-${currentIndex * 100}%)`;
  }

  nextBtn.addEventListener('click', () => {
    currentIndex++;
    updateCarousel();
  });

  prevBtn.addEventListener('click', () => {
    currentIndex--;
    updateCarousel();
  });

  updateCarousel();
}
