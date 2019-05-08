<template>
    <el-form-item :label="label" :required="required" :prop="prop" :rules="rules" :error="error" :validate-status="validateStatus" :label-width="labelWidth">
        <slot></slot>
    </el-form-item>
</template>

<script>
/**
* 为了统一表单的样式，故做此封装
*/
export default {
  name: 'd2-el-form-item',
  props: {
    label: String,
    labelWidth: {
      type: String,
      default: '80px'
    },
    prop: String,
    required: {
      type: Boolean,
      default: undefined
    },
    rules: [Object, Array],
    error: String,
    validateStatus: String
  }
};
</script>

<style lang="scss" scoped>
</style>
