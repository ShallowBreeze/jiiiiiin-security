<template>
  <d2-container-frame :src="url"/>
</template>

<script>
export default {
  name: 'develop-swagger-ui',
  data() {
    return {
      url: process.env.VUE_APP_SWAGGER_API_URL
    };
  },
  created() {
    console.log('api url', this.url, process.env.VUE_APP_SWAGGER_API_URL)
  }
};
</script>
