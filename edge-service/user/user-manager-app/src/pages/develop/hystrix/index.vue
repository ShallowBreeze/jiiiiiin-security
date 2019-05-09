<template>
  <d2-container-frame :src="url"/>
</template>

<script>
export default {
  name: 'develop-hystrix',
  data() {
    return {
      url: process.env.VUE_APP_HYSTRIX_URL
    };
  }
};
</script>
