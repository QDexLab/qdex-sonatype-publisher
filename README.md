# QDex Sonatype Publisher

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Gradle Plugin](https://img.shields.io/badge/Gradle%20Plugin-8.11.1%2B-brightgreen)](https://plugins.gradle.org/plugin/io.github.qdexlab.sonatypePublisher)

QDex Sonatype Publisher 是一个简化发布流程的 Gradle 插件，帮助开发者将 Java 库快速地发布到 Maven Central 仓库。

## 主要特性

- 🚀 **一键发布**：简化 Maven Central 发布流程
- 🔒 **安全凭证管理**：支持环境变量
- 📦 **多模块支持**：自动处理多模块项目发布
- 🔄 **自动签名**：无缝集成 GPG 签名

## 快速开始

### 添加插件依赖

在项目的 `build.gradle` 文件中添加插件：

```groovy
plugins {
    id "io.github.qdexlab.sonatypePublisher" version "1.0.0"
}
```

### 基本配置

```groovy
publishSonatype {
    // 缺省则默认从gradle.properties获取
    sonatype {
        username = System.getenv("qdex.sonatype.username")
        password = System.getenv("qdex.sonatype.password")
    }
    // 缺省则默认从gradle.properties获取
    signing {
        secretKey = System.getenv("qdex.signing.secretKey")
        password = System.getenv("qdex.signing.password")
    }
    pom {
        name = 'qdex-sonatype-publisher-example'
        description = 'An example for using qdex-sonatype-publisher-plugin'
        url = 'https://github.com/QDexLab/qdex-sonatype-publisher'
        scm {
            connection = 'scm:git:https://github.com/QDexLab/qdex-sonatype-publisher.git'
            developerConnection = 'scm:git:ssh://github.com/QDexLab/qdex-sonatype-publisher.git'
            url = 'https://github.com/QDexLab/qdex-sonatype-publisher'
        }
        licenses {
            license {
                licenseName = 'Apache-2.0'
                licenseUrl = 'https://www.apache.org/licenses/LICENSE-2.0.txt'
            }
        }
        developers {
            developer1 {
                developerId = 'QDexLab'
                developerName = 'QDexLab'
                developerEmail = 'xdqnd2012@163.com'
            }
        }
    }
}
```

### 发布命令

```bash
# 发布到 Maven Central
./gradlew publishSonatype

```

## 环境变量

```bash
# 设置环境变量
export qdex.sonatype.username=your-username
export qdex.sonatype.password=your-password
export qdex.signing.secretKey=your-gpg-secret-key
export qdex.signing.password=your-gpg-passphrase
```

## 多模块项目支持

在根项目的 `build.gradle` 中配置：

```groovy
subprojects {
    apply plugin: 'io.github.qdexlab.sonatypePublisher'

    publishSonatype {
        // ...
    }
}
```

## 常见问题解决

### 1. 认证失败
**解决方案**：
- 确认 Sonatype 账号有发布权限
- 检查环境变量是否正确设置
- 验证 GPG 公钥是否已上传到 keyserver

### 依赖环境
- JDK 1.8
- Gradle 8.11.1


## 许可证

本项目采用 Apache License 2.0 开源协议，详情请查看 [LICENSE](LICENSE) 文件。

---

**使用愉快！** 如果您觉得这个插件有用，请给个 ⭐ 支持一下吧！