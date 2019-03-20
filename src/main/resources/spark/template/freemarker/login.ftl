<#import "./base.ftl" as base>

<@base.base title="Log In" context="login">

    <div class="container container--white mt-3">

        <div class="row">
            <div class="col-sm-12 col-md-3"></div>
            <div class="col-sm-12 col-md-6">

                <div class="card">
                    <div class="card-body">
                        <h2 class="card-title">Log In</h2>
                        <form action="/login" method="POST">

                            <div class="form-group">
                                <label for="username">Username</label>
                                <input type="text" class="form-control form-control-sm" name="username" id="username" />
                            </div>
                            <div class="form-group">
                                <label for="password">Password</label>
                                <input type="password" class="form-control form-control-sm" name="password" id="password" />
                            </div>
                            <div class="form-group text-center">
                                <button type="submit" class="btn btn-primary">Log In</button>
                            </div>
                        </form>
                    </div>
                </div>

            </div>
            <div class="col-sm-12 col-md-3"></div>
        </div>

    </div>

</@base.base>