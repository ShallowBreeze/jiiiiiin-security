<template>
  <div>
    <h1>{{url}}</h1>
    <d2-container-frame :src="url"/>
  </div>
</template>

<script>
export default {
  name: 'develop-zipkin',
  data() {
    return {
      url: process.env.VUE_APP_ZIPKIN_URL
    };
  }
};
</script>
