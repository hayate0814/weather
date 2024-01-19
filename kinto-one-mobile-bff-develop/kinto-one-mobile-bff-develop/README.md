<h1 align="left">Hi 👋, I'm 申込みアプリBFF</h1>
&nbsp;

## 環境要件

- java 17 
- docker


## IDE 設定

IntelliJ IDEA
- plugin追加： google-java-format [設定方法](https://github.com/google/google-java-format)


## ローカル開発環境構築
1. Bffリポジトリをcloneしておく
   ```
   git clone git@github.com:kinto-dev/kinto-one-mobile-bff.git
   ```
   
2. docker-compose起動
    - mysql

   ```
   docker-compose up -d
   ```

## 開発フロー注意点
1. 作業ブランチ命名ルール例   
   featureブランチ: feature/{ticket_number}/{description}   
   fixブランチ: fix/{ticket_number}/{description}   
   junitブランチ: junit/{ticket_number}/{description}   
   ※チケットなし場合は、チケット番号の部分を省略とする

   ```shell
   例：
   feature/KOAPP-163/create-new-device
   ```
2. コミット・メッセージ   
   参照：[Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)   

3. PR作成前に、ソースフォーマッタを実行する
   ```
   ./gradlew googleJavaFormat
   ```


## その他
1. ER図
   ```
   ./storage/er/kinto-mobile-bff.a5er
   ```   

2. DDL(bff用データベース)
   ```
   ./storage/mysql/init/ddl_kinto_mobile_bff.sql
   ```

3. google-java-format例
   ```
   java --add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED \
   --add-exports jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED \
   --add-exports jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED \
   --add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED \
   --add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED -jar \
   ./storage/google-format/google-java-format-1.17.0-all-deps.jar --replace \
   ./src/main/java/com/jp/kinto/app/bff/repository/MemberContractRespository.java
   ```
