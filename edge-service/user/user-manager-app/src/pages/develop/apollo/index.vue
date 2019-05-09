<template>
  <d2-container-frame :src="url"/>
</template>

<script>
export default {
  name: 'develop-apollo',
  data() {
    return {
      url: process.env.VUE_APP_APOLLO_URL
    };
  }
};
</script>
