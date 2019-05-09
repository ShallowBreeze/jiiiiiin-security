<template>
  <d2-container-frame :src="url"/>
</template>

<script>
export default {
  name: 'develop-eureka',
  data() {
    return {
      url: process.env.VUE_APP_EUREKA_URL
    };
  }
};
</script>
