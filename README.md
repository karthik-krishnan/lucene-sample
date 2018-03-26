# Lucene Demo

This project helps to quickly index JSON Documents and search for them.

## Installation & Run

```
  mvn clean spring-boot:run
```

## Usage

### Load Dcouments
```
$ curl -XPOST http://localhost:8080/load -F "file=@src/main/resources/sample.json"
```

### Search for Documents
```
$ curl http://localhost:8080/search\?search=orgName:Freedom
```

## Contributing

Bug reports and pull requests are welcome on GitHub at https://github.com/karthik-krishnan/lucene-demo. This project is intended to be a safe, welcoming space for collaboration, and contributors are expected to adhere to the [Contributor Covenant](http://contributor-covenant.org) code of conduct.


## License

The gem is available as open source under the terms of the [MIT License](http://opensource.org/licenses/MIT).
