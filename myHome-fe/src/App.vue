<script setup>
import { onMounted, ref } from "vue";

const message = ref("hello myHome");
const apiStatus = ref("API 연결 확인 중");

onMounted(async () => {
  try {
    const response = await fetch("/api/hello");
    const data = await response.json();

    message.value = data.message || "hello myHome";
    apiStatus.value = "BE 연결 완료";
  } catch (error) {
    apiStatus.value = "BE 실행 후 다시 확인";
  }
});
</script>

<template>
  <main class="app-shell">
    <section class="hello-panel">
      <p class="eyebrow">myHome FE + BE</p>
      <h1>{{ message }}</h1>
      <p class="status">{{ apiStatus }}</p>
    </section>
  </main>
</template>
