// animation.js
import { gsap } from "gsap";

export function leaveAnimation() {
    return new Promise((resolve) => {
        const tl = gsap.timeline();

        // 使用更精确的选择器
        tl.to(".home-container", {
            duration: 0.8,
            opacity: 0,
            y: 100,
            ease: "power4.in"
        })
            .eventCallback("onComplete", resolve);
    });
}