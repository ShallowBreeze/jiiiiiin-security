<template>
  <d2-container-frame :src="url"/>
</template>

<script>
export default {
  name: 'develop-druid',
  data() {
    return {
      url: process.env.VUE_APP_DRUID_URL
    };
  }
};
</script>
