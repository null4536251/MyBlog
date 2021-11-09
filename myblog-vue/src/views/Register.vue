<template>
  <div>
    <el-container>
      <el-header>
        <img class="mlogo" src="../assets/logo.png" alt="logo图片" />
      </el-header>
      <el-main>
        <el-form :model="loginForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="loginForm.email"></el-input>
          </el-form-item>
          <el-form-item label="用户姓名" prop="username">
            <el-input v-model="loginForm.username"></el-input>
          </el-form-item>
          <el-form-item label="用户密码" prop="password">
            <el-input v-model="loginForm.password"></el-input>
          </el-form-item>



          <el-form-item>
            <el-button type="primary" @click="register">注册</el-button>
            <router-link to="login"><el-button>取消</el-button></router-link>
          </el-form-item>
        </el-form>
      </el-main>
    </el-container>
  </div>
</template>

<script>
export function isEmail (s) {
  return /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((.[a-zA-Z0-9_-]{2,3}){1,2})$/.test(s)
}
let validateEmail = (rule, value, callback) => {
  if (!isEmail(value)) {
    callback(new Error('邮箱格式错误'))
  } else {
    callback()
  }
}
export default {

  name: "Login",
  data () {
    return {
      loginForm: {
        username: '',
        password: '',
        email: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名称', trigger: 'blur' },
          { min: 3, max: 15, message: '长度在 3 到 15 个字符', trigger: 'blur' }

        ],
        password: [
          { required: true, message: '请设置密码', trigger: 'blur' }
        ],
        email: [
          {
            required: true, message: '请填写邮箱', trigger: 'blur'
          }
          ,
          {validator: validateEmail, trigger: "blur"}
        ]
      }
    };
  },
  methods: {
    register () {
      var _this = this
      this.$axios
          .post('/register', {
            username: this.loginForm.username,
            password: this.loginForm.password,
            email: this.loginForm.email
          })
          .then(resp => {
            if (resp.data.code === 200) {
              this.$alert('注册成功', '提示', {
                confirmButtonText: '确定'
              })
              _this.$router.replace('/login')
            } else {
              this.$alert(resp.data.message, '提示', {
                confirmButtonText: '确定'
              })
            }
          })
          .catch(failResponse => {})
    }
  }
}
</script>

<style scoped>
.el-header,
.el-footer {
  background-color: #b3c0d1;
  color: #333;
  text-align: center;
  line-height: 60px;
}

.el-main {
  /* background-color: #e9eef3; */
  color: #333;
  text-align: center;
  line-height: 160px;
}
.mlogo {
  height: 80%;
}

.demo-ruleForm {
  max-width: 500px;
  margin: 0 auto;
}
</style>
